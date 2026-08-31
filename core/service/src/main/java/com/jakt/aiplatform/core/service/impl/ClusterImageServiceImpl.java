package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.common.framework.context.UserContext;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.core.model.domain.ClusterImage;
import com.jakt.aiplatform.core.model.enums.BizErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.ClusterImageStatusEnum;
import com.jakt.aiplatform.core.model.param.ClusterImageQueryParam;
import com.jakt.aiplatform.core.repository.ClusterImageRepository;
import com.jakt.aiplatform.core.service.ClusterImageService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 镜像领域服务实现：状态机 + 操作矩阵 + 失败重试。
 */
@Service
public class ClusterImageServiceImpl implements ClusterImageService {

    /** 构建失败最大重试次数。 */
    private static final int MAX_RETRY = 3;

    private final ClusterImageRepository clusterImageRepository;

    public ClusterImageServiceImpl(ClusterImageRepository clusterImageRepository) {
        this.clusterImageRepository = clusterImageRepository;
    }

    @Override
    public ClusterImage createClusterImage(ClusterImage image) {
        checkNameVersionUnique(image);
        image.setId(null);
        image.setBuildStatus(ClusterImageStatusEnum.DRAFT);
        image.setBuildRetryCount(0);
        image.setCreateBy(UserContext.getUserId().toString());
        image.setUpdateBy(UserContext.getUserId().toString());
        return clusterImageRepository.insert(image);
    }

    @Override
    public int updateClusterImage(ClusterImage image) {
        ClusterImage current = getClusterImage(image.getId());
        AssertUtil.throwErrWhenNull(current, BizErrorCodeEnum.RESOURCE_NOT_FOUND, "镜像不存在");
        checkUpdateAllowed(current);
        // 名称/版本变更需重新校验唯一
        checkNameVersionUnique(image);
        image.setBuildStatus(current.getBuildStatus());
        image.setBuildRetryCount(current.getBuildRetryCount());
        image.setUpdateBy(UserContext.getUserId().toString());
        return clusterImageRepository.update(image);
    }

    @Override
    public int updateByCondition(ClusterImage image) {
        return clusterImageRepository.updateByCondition(image);
    }

    @Override
    public int deleteClusterImage(Long id) {
        ClusterImage current = getClusterImage(id);
        AssertUtil.throwErrWhenNull(current, BizErrorCodeEnum.RESOURCE_NOT_FOUND, "镜像不存在");
        checkDeleteAllowed(current);
        if (current.getBuildStatus() == ClusterImageStatusEnum.PUBLISHED) {
            return physicalDeleteClusterImage(id);
        }
        return clusterImageRepository.deleteById(id);
    }

    @Override
    public int physicalDeleteClusterImage(Long id) {
        // TODO(脚本未定): 调 delete_image.sh 删除 Harbor artifact + master/worker ctr images rm + MinIO tar
        // 例如: ssh ubuntu@192.168.3.131 "bash /home/ubuntu/cluster-ci/bin/delete_image.sh <harborRef> <tarName>"
        return clusterImageRepository.deleteById(id);
    }

    @Override
    public void buildClusterImage(Long id) {
        ClusterImage current = getClusterImage(id);
        AssertUtil.throwErrWhenNull(current, BizErrorCodeEnum.RESOURCE_NOT_FOUND, "镜像不存在");
        // DRAFT/BUILD_FAILED 正常可构建；BUILDING 放行用于"卡死恢复"（进程已死但状态残留时重新触发）
        AssertUtil.throwErrWhenTrue(
                current.getBuildStatus() != ClusterImageStatusEnum.DRAFT
                        && current.getBuildStatus() != ClusterImageStatusEnum.BUILD_FAILED
                        && current.getBuildStatus() != ClusterImageStatusEnum.BUILDING,
                BizErrorCodeEnum.STATUS_NOT_ALLOWED,
                "当前状态(" + current.getBuildStatus().getDesc() + ")不允许构建");
        ClusterImage update = new ClusterImage();
        update.setId(id);
        update.setBuildStatus(ClusterImageStatusEnum.BUILDING);
        update.setBuildRetryCount(0);
        clusterImageRepository.updateByCondition(update);
        // TODO(脚本未定): 异步调 build_image.sh（BUILD=拉git+覆盖Dockerfile / EXTERNAL=docker pull 导入）
        // 构建完成后回调 markBuildResult(id, success)
        // 例如: ssh ubuntu@192.168.3.131 "bash /home/ubuntu/cluster-ci/bin/build_image.sh <imageName> <version> ..."
    }

    @Override
    public void markBuildResult(Long id, boolean success) {
        ClusterImage current = getClusterImage(id);
        AssertUtil.throwErrWhenNull(current, BizErrorCodeEnum.RESOURCE_NOT_FOUND, "镜像不存在");
        if (success) {
            ClusterImage update = new ClusterImage();
            update.setId(id);
            update.setBuildStatus(ClusterImageStatusEnum.PUBLISHED);
            clusterImageRepository.updateByCondition(update);
            return;
        }
        int retry = (current.getBuildRetryCount() == null ? 0 : current.getBuildRetryCount()) + 1;
        ClusterImage update = new ClusterImage();
        update.setId(id);
        update.setBuildRetryCount(retry);
        if (retry < MAX_RETRY) {
            // 自动重试：保持 BUILDING 并再次触发构建
            update.setBuildStatus(ClusterImageStatusEnum.BUILDING);
            clusterImageRepository.updateByCondition(update);
            // TODO(脚本未定): 重新调 build_image.sh，成功后回调 markBuildResult(id, true)
        } else {
            update.setBuildStatus(ClusterImageStatusEnum.BUILD_FAILED);
            clusterImageRepository.updateByCondition(update);
        }
    }

    @Override
    public void saveBuildResult(Long id, String harborRef, String tarName) {
        ClusterImage update = new ClusterImage();
        update.setId(id);
        update.setBuildStatus(ClusterImageStatusEnum.PUBLISHED);
        update.setHarborRef(harborRef);
        update.setTarName(tarName);
        update.setBuildRetryCount(0);
        clusterImageRepository.updateByCondition(update);
    }

    @Override
    public ClusterImage checkPublished(Long id) {
        ClusterImage current = getClusterImage(id);
        AssertUtil.throwErrWhenNull(current, BizErrorCodeEnum.RESOURCE_NOT_FOUND, "镜像不存在");
        AssertUtil.throwErrWhenTrue(
                current.getBuildStatus() != ClusterImageStatusEnum.PUBLISHED,
                BizErrorCodeEnum.STATUS_NOT_ALLOWED,
                "仅已发布的镜像可绑定部署，当前状态: " + current.getBuildStatus().getDesc());
        return current;
    }

    @Override
    public List<ClusterImage> listPublished() {
        ClusterImageQueryParam query = new ClusterImageQueryParam();
        query.setBuildStatus(ClusterImageStatusEnum.PUBLISHED);
        return clusterImageRepository.findList(query);
    }

    @Override
    public ClusterImage getClusterImage(Long id) {
        return clusterImageRepository.findById(id);
    }

    @Override
    public PageResult<ClusterImage> findPage(ClusterImageQueryParam query) {
        return clusterImageRepository.findPage(query);
    }

    @Override
    public void checkUpdateAllowed(ClusterImage image) {
        ClusterImageStatusEnum status = image.getBuildStatus();
        AssertUtil.throwErrWhenTrue(
                status != ClusterImageStatusEnum.DRAFT
                        && status != ClusterImageStatusEnum.BUILD_FAILED,
                BizErrorCodeEnum.STATUS_NOT_ALLOWED,
                "当前状态(" + (status == null ? "未知" : status.getDesc()) + ")不允许修改");
    }

    @Override
    public void checkDeleteAllowed(ClusterImage image) {
        ClusterImageStatusEnum status = image.getBuildStatus();
        AssertUtil.throwErrWhenTrue(
                status == ClusterImageStatusEnum.BUILDING,
                BizErrorCodeEnum.STATUS_NOT_ALLOWED,
                "当前状态(构建中)不允许删除");
    }

    @Override
    public void checkNameVersionUnique(ClusterImage image) {
        ClusterImageQueryParam query = new ClusterImageQueryParam();
        query.setImageName(image.getImageName());
        query.setVersion(image.getVersion());
        ClusterImage exists = clusterImageRepository.findOne(query);
        AssertUtil.throwErrWhenTrue(
                exists != null && !exists.getId().equals(image.getId()),
                BizErrorCodeEnum.IMAGE_EXISTS,
                "镜像名 + 版本已存在: " + image.getImageName() + ":" + image.getVersion());
    }
}
