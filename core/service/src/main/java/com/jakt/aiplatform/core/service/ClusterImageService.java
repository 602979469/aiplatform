package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.ClusterImage;
import com.jakt.aiplatform.core.model.param.ClusterImageQueryParam;

import java.util.List;

/**
 * 镜像领域服务。
 */
public interface ClusterImageService {
    /** 创建镜像（初始草稿）。 */
    ClusterImage createClusterImage(ClusterImage image);

    /** 修改镜像（仅草稿/构建失败）。 */
    int updateClusterImage(ClusterImage image);

    /** 按条件更新（状态流转用）。 */
    int updateByCondition(ClusterImage image);

    /** 删除（草稿/失败直接删；已发布走物理删除）。 */
    int deleteClusterImage(Long id);

    /** 物理删除（已发布：Harbor + 节点 ctr + MinIO + DB）。 */
    int physicalDeleteClusterImage(Long id);

    /** 提交构建（仅草稿/构建失败）。 */
    void buildClusterImage(Long id);

    /** 构建结果回写：成功→PUBLISHED；失败→重试≤3 次。 */
    void markBuildResult(Long id, boolean success);

    /** 查询镜像（校验 PUBLISHED，供 pod 配置绑定）。 */
    ClusterImage checkPublished(Long id);

    /** 已发布镜像下拉列表。 */
    List<ClusterImage> listPublished();

    ClusterImage getClusterImage(Long id);

    PageResult<ClusterImage> findPage(ClusterImageQueryParam query);

    /** 检查修改是否允许（草稿/构建失败）。 */
    void checkUpdateAllowed(ClusterImage image);

    /** 检查删除是否允许（草稿/构建失败/已发布；构建中不允许）。 */
    void checkDeleteAllowed(ClusterImage image);

    /** 校验镜像名+版本唯一。 */
    void checkNameVersionUnique(ClusterImage image);
}
