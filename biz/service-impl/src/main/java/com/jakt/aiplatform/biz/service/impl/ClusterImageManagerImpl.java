package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.ClusterImageManager;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.core.model.domain.ClusterImage;
import com.jakt.aiplatform.core.model.param.ClusterImageQueryParam;
import com.jakt.aiplatform.core.service.ClusterImageService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 镜像管理用例编排：CRUD 与构建走镜像领域服务。
 */
@Service
public class ClusterImageManagerImpl implements ClusterImageManager {

    private final ClusterImageService clusterImageService;

    public ClusterImageManagerImpl(ClusterImageService clusterImageService) {
        this.clusterImageService = clusterImageService;
    }

    @Override
    public ClusterImage createClusterImage(ClusterImage image) {
        ClusterImage created = clusterImageService.createClusterImage(image);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "创建镜像成功 id={} image={}:{}",
                created.getId(), created.getImageName(), created.getVersion());
        return created;
    }

    @Override
    public int updateClusterImage(ClusterImage image) {
        int affected = clusterImageService.updateClusterImage(image);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "更新镜像成功 id={} 影响行数={}", image.getId(), affected);
        return affected;
    }

    @Override
    public int deleteClusterImage(Long id) {
        int affected = clusterImageService.deleteClusterImage(id);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除镜像 id={} 影响行数={}", id, affected);
        return affected;
    }

    @Override
    public void buildClusterImage(Long id) {
        clusterImageService.buildClusterImage(id);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "提交镜像构建 id={}", id);
    }

    @Override
    public ClusterImage getClusterImage(Long id) {
        return clusterImageService.getClusterImage(id);
    }

    @Override
    public PageResult<ClusterImage> pageClusterImages(ClusterImageQueryParam query) {
        return clusterImageService.findPage(query);
    }

    @Override
    public List<ClusterImage> listPublishedImages() {
        return clusterImageService.listPublished();
    }
}
