package com.jakt.aiplatform.biz.service.impl;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.BizErrorCodeEnum;

import cn.hutool.core.util.StrUtil;
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

    public ClusterPodConfigManagerImpl(ClusterPodConfigService clusterPodConfigService,
                                       ClusterK8sService clusterK8sService,
                                       ClusterDeployService clusterDeployService) {
        this.clusterPodConfigService = clusterPodConfigService;
        this.clusterK8sService = clusterK8sService;
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
        int affected = clusterPodConfigService.deleteClusterPodConfig(id);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除业务pod配置成功 id={} 影响行数={}", id, affected);
        return affected;
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
        // 异步执行：成功/失败由用户通过实时管理页排查；部署内部 @Retryable 重试 3 次
        clusterDeployService.deploy(config);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "部署已受理 id={} podName={} versionNo={}",
                id, config.getPodName(), config.getVersionNo());
    }

    @Override
    public void stop(Long id) {
        ClusterPodConfig config = requireConfig(id);
        clusterK8sService.stop(config.getNamespace(), deploymentName(config));
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "停用业务pod成功 id={} deployment={}", id, deploymentName(config));
    }

    @Override
    public void start(Long id) {
        ClusterPodConfig config = requireConfig(id);
        int replicas = resolveReplicas(config.getDeployYaml());
        clusterK8sService.start(config.getNamespace(), deploymentName(config), replicas);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "启用业务pod成功 id={} deployment={} replicas={}",
                id, deploymentName(config), replicas);
    }

    @Override
    public List<ClusterRuntimePod> listRuntimePods() {
        return clusterK8sService.listRuntimePods(listNamespaces());
    }

    @Override
    public String getPodLogs(String podName) {
        ClusterPodConfig config = findConfigByPodName(podName);
        return clusterK8sService.getPodLogs(config.getNamespace(), deploymentName(config));
    }

    @Override
    public List<ClusterRuntimeEvent> getPodEvents(String podName) {
        ClusterPodConfig config = findConfigByPodName(podName);
        return clusterK8sService.getPodEvents(config.getNamespace(), deploymentName(config));
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
     * 按 podName 查配置（取第一条），不存在抛业务异常。
     *
     * @param podName pod 名称
     * @return 配置
     */
    private ClusterPodConfig findConfigByPodName(String podName) {
        ClusterPodConfigQueryParam query = new ClusterPodConfigQueryParam();
        query.setPodName(podName);
        List<ClusterPodConfig> configs = clusterPodConfigService.findList(query);
        AssertUtil.throwErrWhenTrue(configs.isEmpty(), BizErrorCodeEnum.RESOURCE_NOT_FOUND,
                "podName 未配置: " + podName);
        return configs.get(0);
    }

    /**
     * Deployment 命名规则：podName-versionNo（K8s 名称小写化）。
     *
     * @param config 配置
     * @return Deployment 名称
     */
    private String deploymentName(ClusterPodConfig config) {
        return (config.getPodName() + "-" + config.getVersionNo()).toLowerCase();
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
