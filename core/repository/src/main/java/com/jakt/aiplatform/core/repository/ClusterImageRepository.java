package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.ClusterImage;
import com.jakt.aiplatform.core.model.param.ClusterImageQueryParam;

import java.util.List;

/**
 * 镜像表仓储。
 */
public interface ClusterImageRepository {
    ClusterImage findById(Long id);

    ClusterImage findOne(ClusterImageQueryParam query);

    PageResult<ClusterImage> findPage(ClusterImageQueryParam query);

    List<ClusterImage> findList(ClusterImageQueryParam query);

    ClusterImage insert(ClusterImage image);

    int update(ClusterImage image);

    int updateByCondition(ClusterImage image);

    int deleteById(Long id);
}
