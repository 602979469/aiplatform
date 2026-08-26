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
     * @param config 业务 pod 配置（含 podName/versionNo/namespace/git/dockerfile/deployYaml）
     */
    void deploy(ClusterPodConfig config);
}
