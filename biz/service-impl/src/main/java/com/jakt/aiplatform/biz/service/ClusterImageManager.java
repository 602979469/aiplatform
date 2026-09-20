package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.ClusterImage;
import com.jakt.aiplatform.core.model.param.ClusterImageQueryParam;

import java.util.List;

/**
 * 镜像管理用例编排。
 */
public interface ClusterImageManager {

    /**
     * 创建镜像（初始草稿）。
     *
     * @param image 镜像
     * @return 创建后的镜像
     */
    ClusterImage createClusterImage(ClusterImage image);

    /**
     * 修改镜像（仅草稿/构建失败可改）。
     *
     * @param image 镜像（含主键）
     * @return 受影响行数
     */
    int updateClusterImage(ClusterImage image);

    /**
     * 删除镜像。
     *
     * @param id 镜像主键
     * @return 受影响行数
     */
    int deleteClusterImage(Long id);

    /**
     * 提交构建。
     *
     * @param id 镜像主键
     */
    void buildClusterImage(Long id);

    /**
     * 查询镜像。
     *
     * @param id 镜像主键
     * @return 镜像
     */
    ClusterImage getClusterImage(Long id);

    /**
     * 分页查询镜像。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<ClusterImage> pageClusterImages(ClusterImageQueryParam query);

    /**
     * 已发布镜像下拉列表（pod 配置绑定用）。
     *
     * @return 镜像列表
     */
    List<ClusterImage> listPublishedImages();

    /**
     * 构建/导入日志（读 build_log_path，tail 500 行）。
     *
     * @param id 镜像主键
     * @return 日志内容
     */
    String getBuildLog(Long id);
}
