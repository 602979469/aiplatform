package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.domain.ClusterPodConfig;

/**
 * 业务 pod 部署领域服务：编排"生成 Dockerfile/deployment.yaml → 写挂载目录 → SSH 触发构建导入 → apply"。
 *
 * <p>部署为异步执行：调用方（Manager）先校验同名 Deployment 再提交线程池，本服务不阻塞请求。
 */
public interface ClusterDeployService {

    /**
     * 执行一次部署（构建 + 导入 + apply + 更新 last_built_commit）。
     *
     * @param config 业务 pod 配置（含 podName/namespace/git/dockerfile/deployYaml）
     */
    void deploy(ClusterPodConfig config);

    /**
     * 解析 Deployment 名称：优先取配置 deployYaml 中 Deployment 的 metadata.name（用户 YAML 为准），
     * 解析不到时回退 podName。
     *
     * @param config 业务 pod 配置
     * @return Deployment 名称
     */
    String resolveDeploymentName(ClusterPodConfig config);

    /**
     * 删除实例：按配置 deployYaml 删除全部 K8s 资源（Deployment/Service/Ingress），不删配置行。
     *
     * @param config 业务 pod 配置
     */
    void deleteInstance(ClusterPodConfig config);

    /**
     * 读取构建日志（master 挂载目录 apps/{configId}/ 下最新 deploy-*.log）。
     *
     * @param configId 配置主键
     * @return 日志内容；无日志返回空字符串
     */
    String getBuildLog(Long configId);
}
