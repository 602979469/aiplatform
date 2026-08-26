package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.ClusterDashboard;
import com.jakt.aiplatform.core.model.domain.ClusterRuntimeEvent;
import com.jakt.aiplatform.core.model.domain.ClusterRuntimePod;
import com.jakt.aiplatform.core.model.domain.ClusterPodConfig;
import com.jakt.aiplatform.core.model.param.ClusterPodConfigQueryParam;

import java.util.List;

/**
 * 业务pod配置表管理类接口定义
 */
public interface ClusterPodConfigManager {

    /**
     * 创建业务pod配置表
     *
     * @param clusterPodConfig 业务pod配置表
     * @return 创建成功后的业务pod配置表
     */
    ClusterPodConfig createClusterPodConfig(ClusterPodConfig clusterPodConfig);

    /**
     * 按主键查询业务pod配置表
     *
     * @param id 业务pod配置表主键
     * @return 业务pod配置表
     */
    ClusterPodConfig getClusterPodConfig(Long id);

    /**
     * 分页查询业务pod配置表
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<ClusterPodConfig> pageClusterPodConfigs(ClusterPodConfigQueryParam query);

    /**
     * 列表查询业务pod配置表
     *
     * @param query 查询参数
     * @return 业务pod配置表列表
     */
    List<ClusterPodConfig> listClusterPodConfigs(ClusterPodConfigQueryParam query);

    /**
     * 更新业务pod配置表（全量）。
     *
     * @param clusterPodConfig 业务pod配置表（含主键）
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int updateClusterPodConfig(ClusterPodConfig clusterPodConfig);

    /**
     * 按条件更新业务pod配置表（只更新传入的非空字段）。
     *
     * @param clusterPodConfig 业务pod配置表（至少含主键）
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int updateByCondition(ClusterPodConfig clusterPodConfig);

    /**
     * 删除业务pod配置表。
     *
     * @param id 业务pod配置表主键
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int deleteClusterPodConfig(Long id);

    /**
     * 集群大盘数据。
     *
     * @return 集群大盘
     */
    ClusterDashboard getDashboard();

    /**
     * 业务命名空间列表。
     *
     * @return 业务命名空间列表
     */
    List<String> listNamespaces();

    /**
     * 触发部署（异步受理，仅校验是否存在同名 Deployment）。
     *
     * @param id 业务pod配置主键
     */
    void deploy(Long id);

    /**
     * 停用：对应 Deployment 缩容到 0。
     *
     * @param id 业务pod配置主键
     */
    void stop(Long id);

    /**
     * 启用：对应 Deployment 扩容到配置副本数。
     *
     * @param id 业务pod配置主键
     */
    void start(Long id);

    /**
     * 实时管理列表。
     *
     * @return 实时业务 pod 列表
     */
    List<ClusterRuntimePod> listRuntimePods();

    /**
     * 运行 Pod 日志。
     *
     * @param podName pod 名称
     * @return 日志文本
     */
    String getPodLogs(String podName);

    /**
     * 运行事件列表。
     *
     * @param podName pod 名称
     * @return K8s 事件列表
     */
    List<ClusterRuntimeEvent> getPodEvents(String podName);
}
