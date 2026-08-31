package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.ClusterImage;
import com.jakt.aiplatform.core.model.param.ClusterImageQueryParam;

import java.util.List;

/**
 * 镜像管理用例编排。
 */
public interface ClusterImageManager {
    ClusterImage createClusterImage(ClusterImage image);

    int updateClusterImage(ClusterImage image);

    int deleteClusterImage(Long id);

    void buildClusterImage(Long id);

    ClusterImage getClusterImage(Long id);

    PageResult<ClusterImage> pageClusterImages(ClusterImageQueryParam query);

    /** 已发布镜像下拉列表（pod 配置绑定用）。 */
    List<ClusterImage> listPublishedImages();

    /** 构建/导入日志（读 build_log_path，tail 500 行）。 */
    String getBuildLog(Long id);
}
