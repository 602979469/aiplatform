package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.ClusterPodConfig;
import com.jakt.aiplatform.core.model.param.ClusterPodConfigQueryParam;

import java.util.List;

/**
 * 业务pod配置表领域服务
 *
 * 实现类为 ClusterPodConfigServiceImpl（core.service.impl 包）。
 */
public interface ClusterPodConfigService {

    /**
     * 创建业务pod配置表
     *
     * @param clusterPodConfig 业务pod配置表
     * @return 创建后的业务pod配置表（主键已回填）
     */
    ClusterPodConfig createClusterPodConfig(ClusterPodConfig clusterPodConfig);

    /**
     * 更新业务pod配置表（全量）
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
     * 删除业务pod配置表
     *
     * @param id 业务pod配置表主键
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int deleteClusterPodConfig(Long id);

    /**
     * 按主键获取业务pod配置表
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
    PageResult<ClusterPodConfig> findPage(ClusterPodConfigQueryParam query);

    /**
     * 列表查询业务pod配置表
     *
     * @param query 查询参数
     * @return 业务pod配置表列表
     */
    List<ClusterPodConfig> findList(ClusterPodConfigQueryParam query);
}
