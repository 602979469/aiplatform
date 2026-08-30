package com.jakt.aiplatform.core.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.jakt.aiplatform.common.integration.exception.AiIntegrationException;
import com.jakt.aiplatform.common.integration.xuanyuan.XuanYuanWebClient;
import com.jakt.aiplatform.common.integration.xuanyuan.XuanYuanProperties;
import com.jakt.aiplatform.common.util.enums.ThreadPoolEnum;
import com.jakt.aiplatform.common.util.tools.MirrorFileUtil;
import com.jakt.aiplatform.common.util.tools.ThreadPoolUtil;
import com.jakt.aiplatform.core.model.domain.FileInfo;
import com.jakt.aiplatform.core.model.domain.MirrorImageResult;
import com.jakt.aiplatform.core.model.domain.MirrorSearchResponse;
import com.jakt.aiplatform.core.model.enums.FileNamespaceEnum;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.core.repository.FileInfoRepository;
import com.jakt.aiplatform.core.service.AiCapabilityService;
import com.jakt.aiplatform.core.service.AiMirrorSearchService;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
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
 * <p>整体流程见 {@link #search(String, String, String, String)}：
 * 解析名称/版本 → 本地已下载文件检查（已有文件少查厂商）→ 官网按下载量搜候选仓库池 →
 * 并发探测每个仓库的 tag 与架构 → 合并本地与新增结果，最多返回 5 个候选（每厂商 1 个）。
 *
 * <p>单个仓库的 tag 匹配按「精确版本 → AI（IMAGE_VERSION_MATCH）→ 纯代码兜底」三层进行，
 * 每层选出的 tag 都会校验客户端架构，AI 超时或不可用时自动降级。
 */
@Service
public class AiMirrorSearchServiceImpl implements AiMirrorSearchService {

    /** 目标结果数量（候选厂商数，每厂商 1 个镜像）。 */
    private static final int TARGET_RESULTS = 5;

    /** 官网按下载量取的候选仓库池上限。 */
    private static final int CANDIDATE_POOL_SIZE = 30;

    /** 候选仓库并发探测数量 = 需要结果数 × 本系数（避免把候选池全部拉去探测）。 */
    private static final int PROBE_FACTOR = 2;

    /** 单个候选仓库探测结果等待超时（秒），避免个别仓库卡死整次搜索。 */
    private static final int PROBE_TIMEOUT_SECONDS = 15;

    /** AI 版本匹配单次超时（秒），超时降级纯代码匹配。 */
    private static final int AI_TIMEOUT_SECONDS = 5;

    /** AI 版本匹配缓存上限，超限整体清空（按 repo+期望版本维度，量级很小）。 */
    private static final int AI_TAG_CACHE_MAX = 500;

    /** 客户端未指定架构时的默认架构。 */
    private static final String DEFAULT_ARCH = "amd64";

    /** 镜像未显式写版本时的默认版本。 */
    private static final String DEFAULT_TAG = "latest";

    /** AI 版本匹配无结果的缓存哨兵值：空串表示「已匹配过、无结果」，与未缓存区分开。 */
    private static final String AI_NO_MATCH = "";

    /** 镜像加速器场景标识。 */
    private static final String SCENE = "MIRROR_ACCELERATOR";

    /** 版本匹配 AI 能力名。 */
    private static final String CAPABILITY_IMAGE_VERSION_MATCH = "IMAGE_VERSION_MATCH";

    private final XuanYuanWebClient xuanYuanWebClient;

    private final XuanYuanProperties xuanYuanProperties;

    private final AiCapabilityService aiCapabilityService;

    private final ThreadPoolUtil threadPoolUtil;

    /** 文件信息表仓储（docker_image 命名空间存量判断）。 */
    private final FileInfoRepository fileInfoRepository;

    /** AI 版本匹配结果缓存：repo|expectTag -> 命中 tag（空串表示无匹配，避免重复调用）。 */
    private final Map<String, String> aiTagCache = new ConcurrentHashMap<>();

    public AiMirrorSearchServiceImpl(XuanYuanWebClient xuanYuanWebClient,
                                     XuanYuanProperties xuanYuanProperties,
                                     AiCapabilityService aiCapabilityService,
                                     ThreadPoolUtil threadPoolUtil,
                                     FileInfoRepository fileInfoRepository) {
        this.xuanYuanWebClient = xuanYuanWebClient;
        this.xuanYuanProperties = xuanYuanProperties;
        this.aiCapabilityService = aiCapabilityService;
        this.threadPoolUtil = threadPoolUtil;
        this.fileInfoRepository = fileInfoRepository;
    }

    @Override
    public MirrorSearchResponse search(String imageName, String os, String arch, String userAgent) {
        long totalStart = System.currentTimeMillis();
        String resolvedOs = resolveOs(os, userAgent);
        String resolvedArch = resolveArch(arch);

        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "【镜像加速器】【搜索】开始搜索: 输入={}, 客户端={}/{}",
                imageName, resolvedOs, resolvedArch);

        // 1. 解析镜像名称与版本（如 mysql:8 -> mysql / 8）
        String[] nameAndTag = parseImageName(imageName);
        String imageBaseName = nameAndTag[0];
        String expectTag = nameAndTag[1];

        // 2. 本地已下载文件检查：已有几个就少查几个厂商
        List<MirrorImageResult> existingResults = findExistingResults(imageBaseName, expectTag);
        int needVendors = Math.max(0, TARGET_RESULTS - existingResults.size());

        // 3. 官网按下载量搜候选仓库池，并剔除本地已下载的厂商
        List<String> candidateRepos = needVendors == 0
                ? Collections.emptyList()
                : searchCandidateRepos(imageBaseName, existingResults);

        // 4. 并发探测候选仓库，按下载量顺序收满 needVendors 个结果
        List<MirrorImageResult> newResults = probeCandidateRepos(candidateRepos, expectTag, resolvedArch, needVendors);

        // 5. 合并本地与新增结果，最多 TARGET_RESULTS 个
        List<MirrorImageResult> results = mergeResults(existingResults, newResults);

        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "【镜像加速器】【搜索】完成: 共 {} 个结果, 总耗时={}ms",
                results.size(), System.currentTimeMillis() - totalStart);
        return buildResponse(resolvedOs, resolvedArch, results);
    }

    /**
     * 确定搜索目标操作系统：入参为空时从 User-Agent 推断。
     *
     * @param os        客户端显式传入的操作系统
     * @param userAgent 客户端 User-Agent，用于兜底推断
     * @return 解析后的操作系统
     */
    private String resolveOs(String os, String userAgent) {
        return StrUtil.isNotBlank(os) ? os : detectOs(userAgent);
    }

    /**
     * 确定搜索目标架构：入参为空时使用默认架构 {@link #DEFAULT_ARCH}。
     *
     * @param arch 客户端显式传入的架构
     * @return 解析后的架构
     */
    private String resolveArch(String arch) {
        return StrUtil.isNotBlank(arch) ? arch : DEFAULT_ARCH;
    }

    /**
     * 解析镜像名称与版本（阶段 1）。
     *
     * <p>{@code mysql:8} 解析为 {@code ["mysql", "8"]}；未显式写版本时默认 {@link #DEFAULT_TAG}。
     * 版本分隔符取最后一个冒号，且要求其后不含斜杠，避免把仓库地址中的端口误判为版本。
     *
     * @param input 用户输入的镜像名（可能带版本）
     * @return [0] 仓库名，[1] 期望版本
     */
    private String[] parseImageName(String input) {
        long phaseStart = System.currentTimeMillis();
        String name = input;
        String tag = DEFAULT_TAG;
        int idx = input.lastIndexOf(':');
        if (idx > 0 && !input.substring(idx + 1).contains("/")) {
            name = input.substring(0, idx);
            tag = input.substring(idx + 1);
        }
        logPhase("名称解析", phaseStart, "%s -> name=%s, tag=%s", input, name, tag);
        return new String[] { name, tag };
    }

    /**
     * 已入库镜像文件转搜索结果（阶段 2，查 file_info 的 docker_image 命名空间），
     * 厂商用仓库命名空间，架构未知显示多架构。
     *
     * @param baseName 镜像名（不含版本）
     * @param tag      期望版本
     * @return 已入库镜像对应的搜索结果
     */
    private List<MirrorImageResult> findExistingResults(String baseName, String tag) {
        long phaseStart = System.currentTimeMillis();
        List<MirrorImageResult> results = new ArrayList<>();
        String baseSegment = baseName.substring(baseName.lastIndexOf('/') + 1);
        for (String fileName : fileInfoRepository.findOriginalNames(FileNamespaceEnum.DOCKER_IMAGE.getCode())) {
            if (ObjectUtil.isNull(fileName) || !fileName.endsWith(".tar")) {
                continue;
            }
            String body = fileName.substring(0, fileName.length() - 4);
            int idx = body.lastIndexOf('_');
            if (idx <= 0) {
                continue;
            }
            String repo = body.substring(0, idx).replace('_', '/');
            String fileTag = body.substring(idx + 1);
            if (!repo.substring(repo.lastIndexOf('/') + 1).equals(baseSegment)
                    || !MirrorFileUtil.isTagMatch(fileTag, tag)) {
                continue;
            }
            MirrorImageResult result = new MirrorImageResult();
            result.setRepo(repo);
            result.setTag(fileTag);
            result.setFullName(prefixedFullName(repo, fileTag));
            result.setVendor(repo.contains("/") ? repo.substring(0, repo.indexOf('/')) : "其他");
            result.setArch("多架构");
            result.setLocalFileName(fileName);
            result.setLocalFileExists(true);
            FileInfo existing = fileInfoRepository.findOne(FileNamespaceEnum.DOCKER_IMAGE.getCode(), fileName);
            result.setFileId(ObjectUtil.isNull(existing) ? null : existing.getId());
            results.add(result);
        }
        logPhase("本地文件检查", phaseStart, "已存在=%s, 还需查询厂商数=%s",
                results.size(), Math.max(0, TARGET_RESULTS - results.size()));
        return results;
    }

    /**
     * 官网按下载量搜索候选仓库池，并剔除本地已下载镜像对应的仓库（阶段 3）。
     *
     * @param name            镜像名（不含版本）
     * @param existingResults 本地已下载镜像结果，用于排除已覆盖的厂商
     * @return 按下载量从大到小排序的候选仓库，最多 {@link #CANDIDATE_POOL_SIZE} 个
     */
    private List<String> searchCandidateRepos(String name, List<MirrorImageResult> existingResults) {
        long phaseStart = System.currentTimeMillis();
        List<String> repos = searchReposByPulls(name, CANDIDATE_POOL_SIZE);
        Set<String> existingRepos = new HashSet<>();
        for (MirrorImageResult result : existingResults) {
            existingRepos.add(result.getRepo());
        }
        repos.removeIf(existingRepos::contains);
        logPhase("官网搜索", phaseStart, "按下载量候选池=%s", repos.size());
        return repos;
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
     * 并发探测候选仓库，按下载量顺序收满 {@code needVendors} 个结果（阶段 4）。
     *
     * <p>候选池取 {@code needVendors × PROBE_FACTOR} 个并发探测，避免个别仓库查不到合适版本时结果不足；
     * 单个仓库最多等待 {@link #PROBE_TIMEOUT_SECONDS} 秒，失败或超时跳过该仓库。
     *
     * @param candidateRepos 候选仓库列表（按下载量排序）
     * @param expectTag      用户期望版本
     * @param userArch       客户端架构
     * @param needVendors    还需要的结果数
     * @return 已选中的镜像结果，数量不超过 needVendors
     */
    private List<MirrorImageResult> probeCandidateRepos(List<String> candidateRepos, String expectTag,
                                                        String userArch, int needVendors) {
        long phaseStart = System.currentTimeMillis();
        List<MirrorImageResult> newResults = new ArrayList<>();
        if (needVendors > 0 && CollUtil.isNotEmpty(candidateRepos)) {
            List<String> candidates = candidateRepos.subList(0,
                    Math.min(candidateRepos.size(), needVendors * PROBE_FACTOR));
            ThreadPoolTaskExecutor executor = threadPoolUtil.getExecutor(ThreadPoolEnum.MIRROR_SEARCH);
            List<CompletableFuture<MirrorImageResult>> futures = new ArrayList<>();
            for (String repo : candidates) {
                futures.add(CompletableFuture.supplyAsync(
                        () -> buildResultFromWeb(repo, expectTag, userArch), executor));
            }
            for (CompletableFuture<MirrorImageResult> future : futures) {
                if (newResults.size() >= needVendors) {
                    break;
                }
                try {
                    MirrorImageResult result = future.get(PROBE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                    if (ObjectUtil.isNotNull(result)) {
                        newResults.add(result);
                    }
                } catch (Exception e) {
                    LoggerUtil.warn(LogFileEnum.COMMON_ERROR,
                            "【镜像加速器】【搜索】仓库探测异常: {}", e.getMessage());
                }
            }
        }
        logPhase("版本+架构匹配", phaseStart, "新增结果数=%s", newResults.size());
        return newResults;
    }

    /**
     * 合并本地已下载与本次新增的镜像结果（阶段 5），本地结果优先，最多 {@link #TARGET_RESULTS} 个。
     *
     * @param existingResults 本地已下载的镜像结果
     * @param newResults      本次并发探测新增的镜像结果
     * @return 合并后的结果列表
     */
    private List<MirrorImageResult> mergeResults(List<MirrorImageResult> existingResults,
                                                 List<MirrorImageResult> newResults) {
        List<MirrorImageResult> results = new ArrayList<>(existingResults);
        for (MirrorImageResult result : newResults) {
            if (results.size() >= TARGET_RESULTS) {
                break;
            }
            results.add(result);
        }
        return results;
    }

    /**
     * 组装搜索响应：携带解析出的操作系统、架构与结果列表。
     *
     * @param os      解析后的操作系统
     * @param arch    解析后的架构
     * @param results 最终镜像结果列表
     * @return 搜索响应
     */
    private MirrorSearchResponse buildResponse(String os, String arch, List<MirrorImageResult> results) {
        MirrorSearchResponse response = new MirrorSearchResponse();
        response.setOs(os);
        response.setArch(arch);
        response.setResults(results);
        return response;
    }

    /**
     * 查询仓库 tags 第一页，按「精确版本 → AI 匹配 → 纯代码兜底」选出满足架构的 tag。
     * 单个仓库接口失败时返回 null 跳过该仓库，不影响整体搜索。
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
        JSONArray tags = ObjectUtil.isNull(json) ? null : json.getJSONArray("tags");
        if (ObjectUtil.isNull(tags) || CollUtil.isEmpty(tags)) {
            LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "【镜像加速器】【搜索】仓库无匹配版本: {}:{}", repo, expectTag);
            return null;
        }

        // 去重 tag 名（官网 tags 接口同一 tag 名会重复返回多条）
        List<String> tagNames = collectTagNames(tags);

        // 第一层：精确版本命中（11 / 11.0.32 / 11-jdk），直接采用并跳过 AI
        String directTag = findDirectVersionTag(tagNames, expectTag);
        if (StrUtil.isNotBlank(directTag)) {
            MirrorImageResult result = buildWithTag(repo, directTag, userArch, tags, namespace);
            if (ObjectUtil.isNotNull(result)) {
                LoggerUtil.info(LogFileEnum.BIZ_SERVICE,
                        "【镜像加速器】【搜索】精确版本命中，跳过 AI: {}:{}", repo, directTag);
                return result;
            }
        }

        // 第二层：AI 版本匹配
        String aiTag = aiMatchVersion(repo, expectTag, tagNames);
        if (StrUtil.isNotBlank(aiTag)) {
            MirrorImageResult result = buildWithTag(repo, aiTag, userArch, tags, namespace);
            if (ObjectUtil.isNotNull(result)) {
                return result;
            }
        }

        // 第三层：AI 不可用或所选 tag 架构不匹配时，按版本匹配度（等值 > 主版本前缀 > 其它）取第一个满足架构的 tag
        tagNames.sort(Comparator.comparingInt((String t) -> versionScore(t, expectTag)).reversed());
        for (String tag : tagNames) {
            MirrorImageResult result = buildWithTag(repo, tag, userArch, tags, namespace);
            if (ObjectUtil.isNotNull(result)) {
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
     * 提取并去重 tag 名列表（官网 tags 接口同一 tag 名会重复返回多条）。
     *
     * @param tags 官网返回的 tags 数组
     * @return 去重后的 tag 名列表
     */
    private List<String> collectTagNames(JSONArray tags) {
        List<String> tagNames = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < tags.size(); i++) {
            String tagName = tags.getJSONObject(i).getString("name");
            if (StrUtil.isBlank(tagName) || !seen.add(tagName)) {
                continue;
            }
            tagNames.add(tagName);
        }
        return tagNames;
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
            ThreadPoolTaskExecutor executor = threadPoolUtil.getExecutor(ThreadPoolEnum.MIRROR_SEARCH);
            CompletableFuture<String> future = CompletableFuture.supplyAsync(
                    () -> invokeVersionMatch(repo, expectTag, tagNames), executor);
            String raw;
            try {
                raw = future.get(AI_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            } catch (TimeoutException te) {
                LoggerUtil.warn(LogFileEnum.BIZ_SERVICE,
                        "【镜像加速器】【AI】版本匹配超时(>{}s)，降级纯代码匹配: repo={}, 期望={}",
                        AI_TIMEOUT_SECONDS, repo, expectTag);
                aiTagCache.put(cacheKey, AI_NO_MATCH);
                return null;
            }
            JSONObject json = parseAiJson(raw);
            JSONArray matchArray = ObjectUtil.isNull(json) ? null : json.getJSONArray("matches");
            if (ObjectUtil.isNotNull(matchArray) && CollUtil.isNotEmpty(matchArray)) {
                String tag = matchArray.getJSONObject(0).getString("tag");
                if (isValidAiTag(tag, expectTag, tagNames)) {
                    picked = tag;
                }
            }
            if (ObjectUtil.isNull(picked)) {
                String fallback = ObjectUtil.isNull(json) ? null : json.getString("fallback");
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
        aiTagCache.put(cacheKey, ObjectUtil.isNull(picked) ? AI_NO_MATCH : picked);
        return picked;
    }

    /** 单独调用 AI 能力，供超时 Future 包装（异常会随 Future 抛出）。 */
    private String invokeVersionMatch(String repo, String expectTag, List<String> tagNames) {
        return aiCapabilityService.invoke(SCENE, CAPABILITY_IMAGE_VERSION_MATCH,
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
        if (StrUtil.isBlank(expectTag) || DEFAULT_TAG.equals(expectTag)) {
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
            if (ObjectUtil.isNull(prefixHit) && versionScore(tag, expectTag) > 0) {
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
            String supportedArches = collectSupportedArches(tagObj.getJSONArray("images"), userArch);
            if (ObjectUtil.isNull(supportedArches)) {
                return null;
            }
            MirrorImageResult result = new MirrorImageResult();
            result.setRepo(repo);
            result.setTag(tag);
            result.setFullName(prefixedFullName(repo, tag));
            result.setVendor(namespace);
            result.setArch(supportedArches);
            result.setLocalFileName(MirrorFileUtil.buildFileName(repo, tag));
            refreshLocalFile(result);
            LoggerUtil.info(LogFileEnum.BIZ_SERVICE,
                    "【镜像加速器】【搜索】选定镜像: {}:{} , 架构={}", repo, tag, supportedArches);
            return result;
        }
        return null;
    }

    /**
     * 解析 AI 返回的 JSON（兼容代码块包裹）。
     */
    private JSONObject parseAiJson(String text) {
        String content = ObjectUtil.isNull(text) ? "" : text.trim();
        if (content.startsWith("```")) {
            content = content.replaceFirst("^```[a-zA-Z]*\\s*", "").replaceAll("```\\s*$", "").trim();
        }
        return JSON.parseObject(content);
    }

    /**
     * 架构匹配：优先 linux 系统，同架构下再退而求其次。
     */
    private String collectSupportedArches(JSONArray images, String userArch) {
        if (ObjectUtil.isNull(images)) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        boolean matched = false;
        for (int i = 0; i < images.size(); i++) {
            JSONObject image = images.getJSONObject(i);
            String arch = image.getString("architecture");
            if (StrUtil.isBlank(arch)) {
                continue;
            }
            // 保持 registry 原始架构列表（不修剪、不去重，可能很长）
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(arch);
            if (arch.equalsIgnoreCase(userArch)) {
                matched = true;
            }
        }
        if (!matched || builder.length() == 0) {
            return null;
        }
        return builder.toString();
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
     * 刷新本地文件状态（本地磁盘检查，很快）。
     */
    private void refreshLocalFile(MirrorImageResult result) {
        FileInfo existing = fileInfoRepository.findOne(
                FileNamespaceEnum.DOCKER_IMAGE.getCode(), result.getLocalFileName());
        result.setLocalFileExists(!ObjectUtil.isNull(existing));
        result.setFileId(ObjectUtil.isNull(existing) ? null : existing.getId());
    }

    /**
     * 拼接带加速器前缀的镜像完整名称（如 docker.xuanyuan.run/circleci/openjdk:latest）。
     *
     * @param repo 仓库路径
     * @param tag  版本号
     * @return 完整名称
     */
    private String prefixedFullName(String repo, String tag) {
        return resolveRegistryHost() + "/" + repo + ":" + tag;
    }

    /**
     * 解析注册表主机（去协议前缀与尾部斜杠）。
     *
     * @return 注册表主机
     */
    private String resolveRegistryHost() {
        String url = xuanYuanProperties.getRegistryUrl();
        if (StrUtil.isBlank(url)) {
            return "docker.xuanyuan.run";
        }
        return url.replaceFirst("^https?://", "").replaceAll("/+$", "");
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
