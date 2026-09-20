package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.ClusterImage;
import com.jakt.aiplatform.core.model.param.ClusterImageQueryParam;

import java.util.List;

/**
 * 镜像表仓储：封装 Mapper，对外只暴露领域模型，不暴露 DO/DalQuery/DalResult。
 */
public interface ClusterImageRepository {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 镜像领域模型
     */
    ClusterImage findById(Long id);

    /**
     * 按条件查询单条。
     *
     * @param query 查询参数
     * @return 镜像领域模型；未查询到返回 null
     */
    ClusterImage findOne(ClusterImageQueryParam query);

    /**
     * 分页查询。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<ClusterImage> findPage(ClusterImageQueryParam query);

    /**
     * 列表查询。
     *
     * @param query 查询参数
     * @return 镜像列表
     */
    List<ClusterImage> findList(ClusterImageQueryParam query);

    /**
     * 新增。
     *
     * @param image 镜像
     * @return 新增后的镜像；主键已回填到入参，返回同一对象
     */
    ClusterImage insert(ClusterImage image);

    /**
     * 更新（全量）。
     *
     * @param image 镜像（含主键）
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int update(ClusterImage image);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新）。
     *
     * @param image 镜像（至少含主键）
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int updateByCondition(ClusterImage image);

    /**
     * 按主键删除。
     *
     * @param id 主键
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int deleteById(Long id);
}
