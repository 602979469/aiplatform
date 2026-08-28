package com.jakt.aiplatform.biz.service.impl;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.framework.result.Result;
import com.jakt.aiplatform.common.framework.template.BizTemplate;
import com.jakt.aiplatform.common.framework.template.TransactionTemplate;
import com.jakt.aiplatform.common.util.error.CommonErrorCode;
import com.jakt.aiplatform.core.model.enums.BizErrorCodeEnum;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollUtil;
import com.jakt.aiplatform.biz.service.ClusterPodConfigManager;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.exception.AiPlatformException;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.core.model.domain.ClusterDashboard;
import com.jakt.aiplatform.core.model.domain.ClusterPodConfig;
import com.jakt.aiplatform.core.model.domain.ClusterRuntimeEvent;
import com.jakt.aiplatform.core.model.domain.ClusterRuntimePod;
import com.jakt.aiplatform.core.model.enums.ClusterPodConfigStatusEnum;
import com.jakt.aiplatform.core.model.enums.BizNamespaceEnum;
import com.jakt.aiplatform.core.model.param.ClusterPodConfigQueryParam;
import com.jakt.aiplatform.core.service.ClusterK8sService;
import com.jakt.aiplatform.core.service.ClusterPodConfigService;
import com.jakt.aiplatform.core.service.ClusterDeployService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 集群管理业务编排：配置 CRUD 走配置领域服务，集群操作走 K8s 领域服务。
 */
@Service
public class ClusterPodConfigManagerImpl implements ClusterPodConfigManager {

    /** 业务命名空间环境变量。 */
    private static final String NAMESPACES_ENV = "AIPLATFORM_BIZ_NAMESPACES";

    /** 配置领域服务。 */
    private final ClusterPodConfigService clusterPodConfigService;

    /** K8s 领域服务。 */
    private final ClusterK8sService clusterK8sService;

    /** 部署领域服务。 */
    private final ClusterDeployService clusterDeployService;

    /** 事务模板。 */
    private final TransactionTemplate transactionTemplate;

    public ClusterPodConfigManagerImpl(ClusterPodConfigService clusterPodConfigService,
                                       ClusterK8sService clusterK8sService,
                                       TransactionTemplate transactionTemplate,
                                       ClusterDeployService clusterDeployService) {
        this.clusterPodConfigService = clusterPodConfigService;
        this.clusterK8sService = clusterK8sService;
        this.transactionTemplate = transactionTemplate;
        this.clusterDeployService = clusterDeployService;
    }

    @Override
    public ClusterPodConfig createClusterPodConfig(ClusterPodConfig clusterPodConfig) {
        ClusterPodConfig created = clusterPodConfigService.createClusterPodConfig(clusterPodConfig);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "创建业务pod配置成功 id={}", created.getId());
        return created;
    }

    @Override
    public ClusterPodConfig getClusterPodConfig(Long id) {
        return clusterPodConfigService.getClusterPodConfig(id);
    }

    @Override
    public PageResult<ClusterPodConfig> pageClusterPodConfigs(ClusterPodConfigQueryParam query) {
        return clusterPodConfigService.findPage(query);
    }

    @Override
    public List<ClusterPodConfig> listClusterPodConfigs(ClusterPodConfigQueryParam query) {
        return clusterPodConfigService.findList(query);
    }

    @Override
    public int updateClusterPodConfig(ClusterPodConfig clusterPodConfig) {
        int affected = clusterPodConfigService.updateClusterPodConfig(clusterPodConfig);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "更新业务pod配置成功 id={} 影响行数={}",
                clusterPodConfig.getId(), affected);
        return affected;
    }

    @Override
    public int updateByCondition(ClusterPodConfig clusterPodConfig) {
        int affected = clusterPodConfigService.updateByCondition(clusterPodConfig);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "按条件更新业务pod配置成功 id={} 影响行数={}",
                clusterPodConfig.getId(), affected);
        return affected;
    }

    @Override
    public int deleteClusterPodConfig(Long id) {

        // 验证配置存在且状态允许删除
        ClusterPodConfig clusterPodConfig = requireConfig(id);
        AssertUtil.throwErrWhenTrue(clusterPodConfig.isRuntimeStatus(), BizErrorCodeEnum.STATUS_NOT_ALLOWED, "运行中状态不允许删除");

        // 事务执行删除配置和实例
        Result<Integer> execute = BizTemplate.execute(transactionTemplate, () -> {

            // 删除配置
            int affected = clusterPodConfigService.deleteClusterPodConfig(id);
            AssertUtil.throwErrWhenTrue(affected == 0, BizErrorCodeEnum.DELETE_FAILED, "删除业务pod配置失败");

            // 删除实例
            clusterDeployService.deleteInstance(clusterPodConfig);
            return affected;
        });
        AssertUtil.throwErrWhenFalse(execute.isSuccess(), execute.getErrorCode(),execute.getErrorMessage());
        return execute.getData();
    }

    @Override
    public ClusterDashboard getDashboard() {
        return clusterK8sService.getDashboard();
    }

    @Override
    public List<String> listNamespaces() {
        String envNamespaces = System.getenv(NAMESPACES_ENV);
        if (StrUtil.isNotBlank(envNamespaces)) {
            return Arrays.stream(envNamespaces.split(","))
                    .map(String::trim)
                    .filter(StrUtil::isNotBlank)
                    .distinct()
                    .toList();
        }
        List<String> defaults = new ArrayList<>();
        for (BizNamespaceEnum namespaceEnum : BizNamespaceEnum.values()) {
            defaults.add(namespaceEnum.getCode());
        }
        return defaults;
    }

    @Override
    @Async("asyncThreadPool")
    public void deploy(Long id) {
        ClusterPodConfig config = requireConfig(id);
        // 状态机：BUILDING（构建中）与 RETIRED（弃用）不可部署
        AssertUtil.throwErrWhenTrue(
                ClusterPodConfigStatusEnum.BUILDING == config.getStatus()
                        || ClusterPodConfigStatusEnum.RETIRED == config.getStatus(),
                BizErrorCodeEnum.STATUS_NOT_ALLOWED,
                "当前状态不允许部署");
        // 状态机：允许部署的状态（DRAFT/BUILDING/BUILD_FAILED/PUBLISHED）先置构建中
        clusterPodConfigService.markBuilding(id);
        // 异步执行：成功/失败由用户通过实时管理页排查；部署内部 @Retryable 重试 3 次
        clusterDeployService.deploy(config);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "部署已受理 id={} podName={}",
                id, config.getPodName());
    }

    @Override
    public void stop(Long id) {
        ClusterPodConfig config = requireConfig(id);
        String deploymentName = clusterDeployService.resolveDeploymentName(config);
        clusterK8sService.stop(config.getNamespace(), deploymentName);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "停用业务pod成功 id={} deployment={}", id, deploymentName);
    }

    @Override
    public void start(Long id) {
        ClusterPodConfig config = requireConfig(id);
        String deploymentName = clusterDeployService.resolveDeploymentName(config);
        int replicas = resolveReplicas(config.getDeployYaml());
        clusterK8sService.start(config.getNamespace(), deploymentName, replicas);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "启用业务pod成功 id={} deployment={} replicas={}",
                id, deploymentName, replicas);
    }

    @Override
    public List<ClusterRuntimePod> listRuntimePods() {
        List<ClusterRuntimePod> pods = clusterK8sService.listRuntimePods(listNamespaces());
        fillPodConfigId(pods);
        return pods;
    }

    @Override
    public String getPodLogs(Long configId) {
        ClusterPodConfig config = requireConfig(configId);
        return clusterK8sService.getPodLogs(config.getNamespace(), clusterDeployService.resolveDeploymentName(config));
    }

    @Override
    public List<ClusterRuntimeEvent> getPodEvents(Long configId) {
        ClusterPodConfig config = requireConfig(configId);
        return clusterK8sService.getPodEvents(config.getNamespace(), clusterDeployService.resolveDeploymentName(config));
    }

    @Override
    public void deleteInstance(Long configId) {
        ClusterPodConfig config = requireConfig(configId);
        // 删除 K8s 实例资源，不删配置行
        clusterDeployService.deleteInstance(config);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "业务pod实例已删除 id={} podName={}", configId, config.getPodName());
    }

    @Override
    public String getBuildLog(Long configId) {
        return clusterDeployService.getBuildLog(configId);
    }

    @Override
    public void retire(Long id) {
        clusterPodConfigService.retire(id);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "弃用业务pod配置成功 id={}", id);
    }

    @Override
    public ClusterPodConfig copyConfig(Long id) {
        ClusterPodConfig source = requireConfig(id);
        ClusterPodConfig copy = new ClusterPodConfig();
        copy.setResourceName(source.getResourceName() + "（副本）");
        copy.setPodName(buildCopyPodName(source.getPodName()));
        copy.setNamespace(source.getNamespace());
        copy.setGitUrl(source.getGitUrl());
        copy.setGitBranch(source.getGitBranch());
        copy.setDockerfile(source.getDockerfile());
        copy.setDeployYaml(source.getDeployYaml());
        copy.setAutoRefresh(source.getAutoRefresh());
        copy.setRemark(source.getRemark());
        // 复制配置状态为草稿，不触发部署
        return clusterPodConfigService.createClusterPodConfig(copy);
    }

    /**
     * 按主键查配置，不存在抛业务异常。
     *
     * @param id 配置主键
     * @return 配置
     */
    private ClusterPodConfig requireConfig(Long id) {
        ClusterPodConfig config = clusterPodConfigService.getClusterPodConfig(id);
        AssertUtil.throwErrWhenNull(config, BizErrorCodeEnum.RESOURCE_NOT_FOUND, "业务pod配置不存在");
        return config;
    }

    /**
     * 生成复制后的 podName：原名称 + "-copy"，冲突时追加序号（-copy1、-copy2...）。
     *
     * @param podName 原 pod 名称
     * @return 不冲突的新名称
     */
    private String buildCopyPodName(String podName) {
        String base = podName + "-copy";
        String candidate = base;
        int suffix = 1;
        while (podNameExists(candidate)) {
            candidate = base + suffix;
            suffix++;
        }
        return candidate;
    }

    /**
     * 判断 podName 是否已存在（配置表唯一）。
     *
     * @param podName pod 名称
     * @return 是否存在
     */
    private boolean podNameExists(String podName) {
        ClusterPodConfigQueryParam query = new ClusterPodConfigQueryParam();
        query.setPodName(podName);
        return clusterPodConfigService.findList(query).size() > 0;
    }

    /**
     * 给实时 pod 列表补齐配置 ID：deployment 名可能是 podName（取决于用户 YAML），按 podName 匹配配置。
     *
     * @param pods 实时 pod 列表
     */
    private void fillPodConfigId(List<ClusterRuntimePod> pods) {
        if (CollUtil.isEmpty(pods)) {
            return;
        }
        List<ClusterPodConfig> configs = clusterPodConfigService.findList(new ClusterPodConfigQueryParam());
        for (ClusterRuntimePod pod : pods) {
            for (ClusterPodConfig config : configs) {
                if (config.getPodName().equals(pod.getPodName())) {
                    pod.setPodConfigId(config.getId());
                    pod.setPodName(config.getPodName());
                    break;
                }
            }
        }
    }

    /**
     * 从 Deployment YAML 解析期望副本数，解析不到按默认 1。
     *
     * @param deployYaml Deployment YAML
     * @return 副本数
     */
    private int resolveReplicas(String deployYaml) {
        if (deployYaml == null) {
            return 1;
        }
        // TODO 待接入 YAML 解析：读取 spec.replicas，缺省默认 1
        return 1;
    }
}
