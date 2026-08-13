package com.jakt.aiplatform.core.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.jakt.aiplatform.common.integration.exception.AiIntegrationException;
import com.jakt.aiplatform.common.integration.xuanyuan.XuanYuanWebClient;
import com.jakt.aiplatform.common.util.tools.MirrorFileUtil;
import com.jakt.aiplatform.core.model.domain.MirrorImageResult;
import com.jakt.aiplatform.core.model.domain.MirrorSearchResponse;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.service.AiCapabilityService;
import com.jakt.aiplatform.core.service.AiMirrorSearchService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private static final String SCENE = "MIRROR_ACCELERATOR";

    private final XuanYuanWebClient xuanYuanWebClient;

    private final AiCapabilityService aiCapabilityService;

    public AiMirrorSearchServiceImpl(XuanYuanWebClient xuanYuanWebClient,
                                     AiCapabilityService aiCapabilityService) {
        this.xuanYuanWebClient = xuanYuanWebClient;
        this.aiCapabilityService = aiCapabilityService;
    }

    @Override
    public MirrorSearchResponse search(String imageName, String os, String arch, String userAgent) {
        long totalStart = System.currentTimeMillis();
        String resolvedOs = StrUtil.isNotBlank(os) ? os : detectOs(userAgent);
        String resolvedArch = StrUtil.isNotBlank(arch) ? arch : "amd64";

        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "【镜像加速器】【搜索】开始搜索: 输入={}, 客户端={}/{}",
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

        // 4. 按下载量顺序逐仓库取第一个满足版本+架构的 tag，凑够 needVendors 个
        phaseStart = System.currentTimeMillis();
        List<MirrorImageResult> newResults = new ArrayList<>();
        for (String repo : repos) {
            if (newResults.size() >= needVendors) {
                break;
            }
            MirrorImageResult result = buildResultFromWeb(repo, tag, resolvedArch);
            if (result != null) {
                newResults.add(result);
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

        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "【镜像加速器】【搜索】完成: 共 {} 个结果, 总耗时={}ms",
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
            AiPlatformLoggerUtil.warn(LogFileEnum.BIZ_SERVICE, "【镜像加速器】【搜索】仓库tags查询失败, 跳过: {}, 错误={}",
                    repo, e.getMessage());
            return null;
        }
        JSONArray tags = json == null ? null : json.getJSONArray("tags");
        if (tags == null || tags.isEmpty()) {
            AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "【镜像加速器】【搜索】仓库无匹配版本: {}:{}", repo, expectTag);
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

        // AI 版本匹配优先
        String aiTag = aiMatchVersion(repo, expectTag, tagNames);
        if (StrUtil.isNotBlank(aiTag)) {
            MirrorImageResult result = buildWithTag(repo, aiTag, userArch, tags, namespace);
            if (result != null) {
                return result;
            }
        }

        // AI 不可用或所选 tag 架构不匹配时：兜底取第一个满足架构的 tag
        for (String tag : tagNames) {
            MirrorImageResult result = buildWithTag(repo, tag, userArch, tags, namespace);
            if (result != null) {
                AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE,
                        "【镜像加速器】【搜索】AI 未选用，纯代码兜底: {}:{} , 架构={}", repo, tag, result.getArch());
                return result;
            }
        }
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE,
                "【镜像加速器】【搜索】仓库无匹配架构镜像: {}:{} , 客户端架构={}", repo, expectTag, userArch);
        return null;
    }

    /**
     * AI 版本匹配：把第一页 tag 名列表交给 IMAGE_VERSION_MATCH，返回最符合期望版本的 tag。
     */
    private String aiMatchVersion(String repo, String expectTag, List<String> tagNames) {
        try {
            String raw = aiCapabilityService.invoke(SCENE, "IMAGE_VERSION_MATCH",
                    "基础镜像名: " + repo + "\n用户期望版本: " + expectTag + "\ntag列表: " + String.join(",", tagNames));
            JSONObject json = parseAiJson(raw);
            JSONArray matchArray = json == null ? null : json.getJSONArray("matches");
            if (matchArray != null && !matchArray.isEmpty()) {
                String tag = matchArray.getJSONObject(0).getString("tag");
                if (StrUtil.isNotBlank(tag) && tagNames.contains(tag)) {
                    return tag;
                }
            }
            String fallback = json == null ? null : json.getString("fallback");
            if (StrUtil.isNotBlank(fallback) && tagNames.contains(fallback)) {
                return fallback;
            }
        } catch (Exception e) {
            AiPlatformLoggerUtil.error(LogFileEnum.COMMON_ERROR,
                    "【镜像加速器】【AI】版本匹配解析失败: repo={}, 期望={}", repo, expectTag);
        }
        return null;
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
            AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE,
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
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "【镜像加速器】【搜索】阶段[{}]耗时={}ms | {}",
                phase, System.currentTimeMillis() - start, String.format(detail, args));
    }
}
