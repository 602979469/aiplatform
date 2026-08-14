package com.jakt.aiplatform.core.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.jakt.aiplatform.common.integration.exception.AiIntegrationException;
import com.jakt.aiplatform.common.integration.xuanyuan.XuanYuanWebClient;
import com.jakt.aiplatform.common.util.enums.ThreadPoolEnum;
import com.jakt.aiplatform.common.util.tools.MirrorFileUtil;
import com.jakt.aiplatform.common.util.tools.ThreadPoolUtil;
import com.jakt.aiplatform.core.model.domain.MirrorImageResult;
import com.jakt.aiplatform.core.model.domain.MirrorSearchResponse;
import com.jakt.aiplatform.common.util.enums.LogFileEnum;
import com.jakt.aiplatform.common.util.tools.LoggerUtil;
import com.jakt.aiplatform.core.service.AiCapabilityService;
import com.jakt.aiplatform.core.service.AiMirrorSearchService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 镜像搜索领域服务实现。
 *
 * <p>流程：本地解析名称/版本 → 本地文件检查（已有文件少查厂商）→ 官网搜索下载量最高的仓库 →
 * 逐仓库取第一个满足架构的 tag。版本号匹配由 AI（IMAGE_VERSION_MATCH）完成，最多返回 5 个候选（每厂商 1 个）。
 */
@Service
public class AiMirrorSearchServiceImpl implements AiMirrorSearchService {

    /** 目标结果数量（候选厂商数，每厂商 1 个镜像）。 */
    private static final int TARGET_RESULTS = 5;

    /** AI 版本匹配单次超时（秒），超时降级纯代码匹配。 */
    private static final int AI_TIMEOUT_SECONDS = 5;

    /** 候选仓库并发探测数量 = 需要结果数 × 本系数（避免把候选池全部拉去探测）。 */
    private static final int PROBE_FACTOR = 2;

    /** 单个候选仓库探测结果等待超时（秒），避免个别仓库卡死整次搜索。 */
    private static final int PROBE_TIMEOUT_SECONDS = 15;

    /** AI 版本匹配缓存上限，超限整体清空（按 repo+期望版本维度，量级很小）。 */
    private static final int AI_TAG_CACHE_MAX = 500;

    private static final String SCENE = "MIRROR_ACCELERATOR";

    private final XuanYuanWebClient xuanYuanWebClient;

    private final AiCapabilityService aiCapabilityService;

    private final ThreadPoolUtil threadPoolUtil;

    /** AI 版本匹配结果缓存：repo|expectTag -> 命中 tag（空串表示无匹配，避免重复调用）。 */
    private final Map<String, String> aiTagCache = new ConcurrentHashMap<>();

    public AiMirrorSearchServiceImpl(XuanYuanWebClient xuanYuanWebClient,
                                     AiCapabilityService aiCapabilityService,
                                     ThreadPoolUtil threadPoolUtil) {
        this.xuanYuanWebClient = xuanYuanWebClient;
        this.aiCapabilityService = aiCapabilityService;
        this.threadPoolUtil = threadPoolUtil;
    }

    @Override
    public MirrorSearchResponse search(String imageName, String os, String arch, String userAgent) {
        long totalStart = System.currentTimeMillis();
        String resolvedOs = StrUtil.isNotBlank(os) ? os : detectOs(userAgent);
        String resolvedArch = StrUtil.isNotBlank(arch) ? arch : "amd64";

        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "【镜像加速器】【搜索】开始搜索: 输入={}, 客户端={}/{}",
                imageName, resolvedOs, resolvedArch);

        // 1. 本地解析名称与版本（如 mysql:8 -> mysql / 8）
        long phaseStart = System.currentTimeMillis();
        String[] parts = splitImageName(imageName);
        String name = parts[0];
        String tag = parts[1];
        logPhase("名称解析", phaseStart, "%s -> name=%s, tag=%s", imageName, name, tag);

        // 2. 本地已下载文件检查：已有几个就少查几个厂商
        phaseStart = System.currentTimeMillis();
        List<MirrorImageResult> existingResults = findExistingResults(name, tag);
        int needVendors = Math.max(0, TARGET_RESULTS - existingResults.size());
        logPhase("本地文件检查", phaseStart, "已存在=%s, 还需查询厂商数=%s", existingResults.size(), needVendors);

        // 3. 官网搜索：按下载量取候选仓库池（最多 30 个，便于往下顺延凑够厂商数）
        phaseStart = System.currentTimeMillis();
        List<String> repos = needVendors == 0 ? new ArrayList<>() : searchReposByPulls(name, 30);
        Set<String> existingRepos = new HashSet<>();
        for (MirrorImageResult result : existingResults) {
            existingRepos.add(result.getRepo());
        }
        repos.removeIf(existingRepos::contains);
        logPhase("官网搜索", phaseStart, "按下载量候选池=%s", repos.size());

        // 4. 并发探测候选仓库（最多 needVendors × PROBE_FACTOR 个），按下载量顺序取前 needVendors 个结果
        phaseStart = System.currentTimeMillis();
        List<MirrorImageResult> newResults = new ArrayList<>();
        if (needVendors > 0 && !repos.isEmpty()) {
            List<String> candidates = repos.subList(0, Math.min(repos.size(), needVendors * PROBE_FACTOR));
            List<CompletableFuture<MirrorImageResult>> futures = new ArrayList<>();
            for (String repo : candidates) {
                futures.add(CompletableFuture.supplyAsync(
                        () -> buildResultFromWeb(repo, tag, resolvedArch),
                        threadPoolUtil.getExecutor(ThreadPoolEnum.MIRROR_SEARCH)));
            }
            for (CompletableFuture<MirrorImageResult> future : futures) {
                if (newResults.size() >= needVendors) {
                    break;
                }
                try {
                    MirrorImageResult result = future.get(PROBE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                    if (result != null) {
                        newResults.add(result);
                    }
                } catch (Exception e) {
                    LoggerUtil.warn(LogFileEnum.COMMON_ERROR,
                            "【镜像加速器】【搜索】仓库探测异常: {}", e.getMessage());
                }
            }
        }
        logPhase("版本+架构匹配", phaseStart, "新增结果数=%s", newResults.size());

        // 5. 合并：本地已有 + 新增，最多 TARGET_RESULTS 个
        List<MirrorImageResult> results = new ArrayList<>(existingResults);
        for (MirrorImageResult result : newResults) {
            if (results.size() >= TARGET_RESULTS) {
                break;
            }
            results.add(result);
        }

        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "【镜像加速器】【搜索】完成: 共 {} 个结果, 总耗时={}ms",
                results.size(), System.currentTimeMillis() - totalStart);
        MirrorSearchResponse response = new MirrorSearchResponse();
        response.setOs(resolvedOs);
        response.setArch(resolvedArch);
        response.setResults(results);
        return response;
    }

    /**
     * 官网搜索仓库并按下载量排序，取前 maxCount 个 Docker Hub 仓库（候选池）。
     */
    private List<String> searchReposByPulls(String query, int maxCount) {
        List<String> repos = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        List<JSONObject> results = xuanYuanWebClient.searchRepos(query, maxCount);
        results.sort((a, b) -> Long.compare(parsePullCount(b.getString("pull_count")),
                parsePullCount(a.getString("pull_count"))));
        for (JSONObject item : results) {
            String repo = normalizeDockerHubRepo(item.getString("id"));
            if (!isDockerHubRepo(repo) || !seen.add(repo)) {
                continue;
            }
            repos.add(repo);
            if (repos.size() >= maxCount) {
                break;
            }
        }
        return repos;
    }

    /**
     * 查询仓库 tags 第一页，AI 版本匹配选出合适 tag 后校验架构；AI 失败时兜底取第一个满足架构的 tag。
     * 单个仓库接口失败时跳过该仓库，不影响整体搜索。
     */
    private MirrorImageResult buildResultFromWeb(String repo, String expectTag, String userArch) {
        int slash = repo.indexOf('/');
        String namespace = repo.substring(0, slash);
        String name = repo.substring(slash + 1);
        JSONObject json;
        try {
            json = xuanYuanWebClient.fetchTags(namespace, name, expectTag);
        } catch (AiIntegrationException e) {
            LoggerUtil.warn(LogFileEnum.BIZ_SERVICE, "【镜像加速器】【搜索】仓库tags查询失败, 跳过: {}, 错误={}",
                    repo, e.getMessage());
            return null;
        }
        JSONArray tags = json == null ? null : json.getJSONArray("tags");
        if (tags == null || tags.isEmpty()) {
            LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "【镜像加速器】【搜索】仓库无匹配版本: {}:{}", repo, expectTag);
            return null;
        }

        // 去重 tag 名（官网 tags 接口同一 tag 名会重复返回多条）
        List<String> tagNames = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < tags.size(); i++) {
            String tagName = tags.getJSONObject(i).getString("name");
            if (StrUtil.isBlank(tagName) || !seen.add(tagName)) {
                continue;
            }
            tagNames.add(tagName);
        }

        // 精确版本优先：等值或主版本前缀（11 / 11.0.32 / 11-jdk）命中直接返回，跳过 AI
        String directTag = findDirectVersionTag(tagNames, expectTag);
        if (directTag != null) {
            MirrorImageResult result = buildWithTag(repo, directTag, userArch, tags, namespace);
            if (result != null) {
                LoggerUtil.info(LogFileEnum.BIZ_SERVICE,
                        "【镜像加速器】【搜索】精确版本命中，跳过 AI: {}:{}", repo, directTag);
                return result;
            }
        }

        // AI 版本匹配优先
        String aiTag = aiMatchVersion(repo, expectTag, tagNames);
        if (StrUtil.isNotBlank(aiTag)) {
            MirrorImageResult result = buildWithTag(repo, aiTag, userArch, tags, namespace);
            if (result != null) {
                return result;
            }
        }

        // AI 不可用或所选 tag 架构不匹配时：兜底按版本匹配度（等值 > 主版本前缀 > 其它）取第一个满足架构的 tag
        tagNames.sort(Comparator.comparingInt((String t) -> versionScore(t, expectTag)).reversed());
        for (String tag : tagNames) {
            MirrorImageResult result = buildWithTag(repo, tag, userArch, tags, namespace);
            if (result != null) {
                LoggerUtil.info(LogFileEnum.BIZ_SERVICE,
                        "【镜像加速器】【搜索】AI 未选用，纯代码兜底: {}:{} , 架构={}", repo, tag, result.getArch());
                return result;
            }
        }
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE,
                "【镜像加速器】【搜索】仓库无匹配架构镜像: {}:{} , 客户端架构={}", repo, expectTag, userArch);
        return null;
    }

    /**
     * AI 版本匹配：把第一页 tag 名列表交给 IMAGE_VERSION_MATCH，返回最符合期望版本的 tag。
     */
    private String aiMatchVersion(String repo, String expectTag, List<String> tagNames) {
        String cacheKey = repo + "|" + expectTag;
        if (aiTagCache.containsKey(cacheKey)) {
            String cached = aiTagCache.get(cacheKey);
            return StrUtil.isBlank(cached) ? null : cached;
        }
        String picked = null;
        try {
            CompletableFuture<String> future = CompletableFuture.supplyAsync(
                    () -> invokeVersionMatch(repo, expectTag, tagNames),
                    threadPoolUtil.getExecutor(ThreadPoolEnum.MIRROR_SEARCH));
            String raw;
            try {
                raw = future.get(AI_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            } catch (TimeoutException te) {
                LoggerUtil.warn(LogFileEnum.BIZ_SERVICE,
                        "【镜像加速器】【AI】版本匹配超时(>{}s)，降级纯代码匹配: repo={}, 期望={}",
                        AI_TIMEOUT_SECONDS, repo, expectTag);
                aiTagCache.put(cacheKey, "");
                return null;
            }
            JSONObject json = parseAiJson(raw);
            JSONArray matchArray = json == null ? null : json.getJSONArray("matches");
            if (matchArray != null && !matchArray.isEmpty()) {
                String tag = matchArray.getJSONObject(0).getString("tag");
                if (isValidAiTag(tag, expectTag, tagNames)) {
                    picked = tag;
                }
            }
            if (picked == null) {
                String fallback = json == null ? null : json.getString("fallback");
                if (isValidAiTag(fallback, expectTag, tagNames)) {
                    picked = fallback;
                }
            }
        } catch (Exception e) {
            LoggerUtil.error(LogFileEnum.COMMON_ERROR, e,
                    "【镜像加速器】【AI】版本匹配解析失败: repo={}, 期望={}, 异常={}", repo, expectTag, e.getMessage());
        }
        if (aiTagCache.size() >= AI_TAG_CACHE_MAX) {
            aiTagCache.clear();
        }
        aiTagCache.put(cacheKey, picked == null ? "" : picked);
        return picked;
    }

    /** 单独调用 AI 能力，供超时 Future 包装（异常会随 Future 抛出）。 */
    private String invokeVersionMatch(String repo, String expectTag, List<String> tagNames) {
        return aiCapabilityService.invoke(SCENE, "IMAGE_VERSION_MATCH",
                "基础镜像名: " + repo + "\n用户期望版本: " + expectTag + "\ntag列表: " + String.join(",", tagNames));
    }

    /**
     * AI 返回的 tag 必须真实存在于列表，且等值或以期望版本主版本开头
     * （拒绝 27-ea-11 匹配 11 这类"子串误判"）。
     */
    private boolean isValidAiTag(String tag, String expectTag, List<String> tagNames) {
        if (StrUtil.isBlank(tag) || !tagNames.contains(tag)) {
            return false;
        }
        if (StrUtil.isBlank(expectTag) || "latest".equals(expectTag)) {
            return true;
        }
        return versionScore(tag, expectTag) > 0;
    }

    /**
     * 直接版本命中：tag 等值期望版本，或以期望版本+"."/"-" 开头（11 / 11.0.32 / 11-jdk）。
     */
    private String findDirectVersionTag(List<String> tagNames, String expectTag) {
        if (StrUtil.isBlank(expectTag)) {
            return null;
        }
        String prefixHit = null;
        for (String tag : tagNames) {
            if (tag.equals(expectTag)) {
                return tag;
            }
            if (prefixHit == null && versionScore(tag, expectTag) > 0) {
                prefixHit = tag;
            }
        }
        return prefixHit;
    }

    /** 版本匹配度：等值 2，主版本前缀（11. / 11-）1，其它 0。 */
    private int versionScore(String tag, String expectTag) {
        if (StrUtil.isBlank(expectTag)) {
            return 0;
        }
        if (tag.equals(expectTag)) {
            return 2;
        }
        if (tag.startsWith(expectTag + ".") || tag.startsWith(expectTag + "-")) {
            return 1;
        }
        return 0;
    }

    /**
     * 按 tag 名从 tags 列表中取镜像（校验架构），找不到返回 null。
     */
    private MirrorImageResult buildWithTag(String repo, String tag, String userArch, JSONArray tags, String namespace) {
        for (int i = 0; i < tags.size(); i++) {
            JSONObject tagObj = tags.getJSONObject(i);
            if (!tag.equals(tagObj.getString("name"))) {
                continue;
            }
            String matchedArch = matchArch(tagObj.getJSONArray("images"), userArch);
            if (matchedArch == null) {
                return null;
            }
            MirrorImageResult result = new MirrorImageResult();
            result.setRepo(repo);
            result.setTag(tag);
            result.setFullName(repo + ":" + tag);
            result.setVendor(namespace);
            result.setArch("支持 " + matchedArch);
            result.setLocalFileName(MirrorFileUtil.buildFileName(repo, tag));
            refreshLocalFile(result);
            LoggerUtil.info(LogFileEnum.BIZ_SERVICE,
                    "【镜像加速器】【搜索】选定镜像: {}:{} , 架构={}", repo, tag, matchedArch);
            return result;
        }
        return null;
    }

    /**
     * 解析 AI 返回的 JSON（兼容代码块包裹）。
     */
    private JSONObject parseAiJson(String text) {
        String content = text == null ? "" : text.trim();
        if (content.startsWith("```")) {
            content = content.replaceFirst("^```[a-zA-Z]*\\s*", "").replaceAll("```\\s*$", "").trim();
        }
        return JSON.parseObject(content);
    }

    /**
     * 架构匹配：优先 linux 系统，同架构下再退而求其次。
     */
    private String matchArch(JSONArray images, String userArch) {
        if (images == null) {
            return null;
        }
        String anyOs = null;
        for (int i = 0; i < images.size(); i++) {
            JSONObject image = images.getJSONObject(i);
            String os = image.getString("os");
            String arch = image.getString("architecture");
            if (StrUtil.isBlank(arch) || !arch.equalsIgnoreCase(userArch)) {
                continue;
            }
            if ("linux".equals(os)) {
                return arch;
            }
            if (anyOs == null) {
                anyOs = arch;
            }
        }
        return anyOs;
    }

    /**
     * 解析下载量文本：1B+ -> 10亿，100M+ -> 1亿，50K+ -> 5万。
     */
    private long parsePullCount(String value) {
        if (StrUtil.isBlank(value)) {
            return 0L;
        }
        String v = value.trim().toUpperCase();
        double multiplier = 1;
        if (v.endsWith("B+")) {
            multiplier = 1_000_000_000L;
            v = v.substring(0, v.length() - 2);
        } else if (v.endsWith("M+")) {
            multiplier = 1_000_000L;
            v = v.substring(0, v.length() - 2);
        } else if (v.endsWith("K+")) {
            multiplier = 1_000L;
            v = v.substring(0, v.length() - 2);
        } else if (v.endsWith("B")) {
            multiplier = 1_000_000_000L;
            v = v.substring(0, v.length() - 1);
        } else if (v.endsWith("M")) {
            multiplier = 1_000_000L;
            v = v.substring(0, v.length() - 1);
        } else if (v.endsWith("K")) {
            multiplier = 1_000L;
            v = v.substring(0, v.length() - 1);
        }
        try {
            return (long) (Double.parseDouble(v) * multiplier);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    /**
     * 本地解析镜像名称与版本。
     */
    private String[] splitImageName(String input) {
        String name = input;
        String tag = "latest";
        int idx = input.lastIndexOf(':');
        if (idx > 0 && !input.substring(idx + 1).contains("/")) {
            name = input.substring(0, idx);
            tag = input.substring(idx + 1);
        }
        return new String[] { name, tag };
    }

    /**
     * 本地已下载文件转搜索结果（厂商用仓库命名空间，架构未知显示多架构）。
     */
    private List<MirrorImageResult> findExistingResults(String baseName, String tag) {
        List<MirrorImageResult> results = new ArrayList<>();
        for (String[] repoTag : MirrorFileUtil.findExistingImages(baseName, tag)) {
            String repo = repoTag[0];
            String fileTag = repoTag[1];
            MirrorImageResult result = new MirrorImageResult();
            result.setRepo(repo);
            result.setTag(fileTag);
            result.setFullName(repo + ":" + fileTag);
            result.setVendor(repo.contains("/") ? repo.substring(0, repo.indexOf('/')) : "其他");
            result.setArch("多架构");
            result.setLocalFileName(MirrorFileUtil.buildFileName(repo, fileTag));
            result.setLocalFileExists(true);
            results.add(result);
        }
        return results;
    }

    /**
     * 刷新本地文件状态（本地磁盘检查，很快）。
     */
    private void refreshLocalFile(MirrorImageResult result) {
        result.setLocalFileExists(MirrorFileUtil.isFileExists(result.getLocalFileName()));
    }

    /**
     * 去 docker.io/ 前缀。
     */
    private String normalizeDockerHubRepo(String repo) {
        if (StrUtil.isNotBlank(repo) && repo.startsWith("docker.io/")) {
            return repo.substring("docker.io/".length());
        }
        return repo;
    }

    /**
     * 是否 Docker Hub 仓库路径：恰好两段，命名空间不含点（排除 gcr.io/quay.io/registry.k8s.io 等其它仓库源）。
     */
    private boolean isDockerHubRepo(String repo) {
        if (StrUtil.isBlank(repo)) {
            return false;
        }
        int firstSlash = repo.indexOf('/');
        if (firstSlash <= 0 || repo.indexOf('/', firstSlash + 1) >= 0) {
            return false;
        }
        String namespace = repo.substring(0, firstSlash);
        return namespace.matches("[a-zA-Z0-9][a-zA-Z0-9-]*");
    }

    /**
     * 从 User-Agent 判断操作系统。
     */
    private String detectOs(String userAgent) {
        if (StrUtil.isBlank(userAgent)) {
            return "未知";
        }
        String ua = userAgent.toLowerCase();
        if (ua.contains("windows")) {
            return "Windows";
        }
        if (ua.contains("mac os") || ua.contains("macintosh")) {
            return "macOS";
        }
        if (ua.contains("android")) {
            return "Android";
        }
        if (ua.contains("iphone") || ua.contains("ipad")) {
            return "iOS";
        }
        if (ua.contains("linux")) {
            return "Linux";
        }
        return "未知";
    }

    /**
     * 阶段计时日志。
     */
    private void logPhase(String phase, long start, String detail, Object... args) {
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "【镜像加速器】【搜索】阶段[{}]耗时={}ms | {}",
                phase, System.currentTimeMillis() - start, String.format(detail, args));
    }
}
