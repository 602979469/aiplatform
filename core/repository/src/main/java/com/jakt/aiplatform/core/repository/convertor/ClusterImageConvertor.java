package com.jakt.aiplatform.core.repository.convertor;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.dal.dataobject.ClusterImageDO;
import com.jakt.aiplatform.common.dal.query.ClusterImageDalQuery;
import com.jakt.aiplatform.common.framework.enums.BaseEnum;
import com.jakt.aiplatform.core.model.domain.ClusterImage;
import com.jakt.aiplatform.core.model.enums.ClusterImageStatusEnum;
import com.jakt.aiplatform.core.model.enums.ClusterImageTypeEnum;
import com.jakt.aiplatform.core.model.param.ClusterImageQueryParam;

/**
 * 镜像表 DO / 领域模型 / 查询参数互转，只存在于 repository。
 */
public final class ClusterImageConvertor {

    private ClusterImageConvertor() {
    }

    public static ClusterImage toModel(ClusterImageDO source) {
        if (source == null) {
            return null;
        }
        ClusterImage target = new ClusterImage();
        target.setId(source.getId());
        target.setImageName(source.getImageName());
        target.setVersion(source.getVersion());
        target.setImageType(BaseEnum.fromCode(ClusterImageTypeEnum.class, source.getImageType()));
        target.setGitUrl(source.getGitUrl());
        target.setGitBranch(source.getGitBranch());
        target.setDockerfile(source.getDockerfile());
        target.setExternalImage(source.getExternalImage());
        target.setHarborRef(source.getHarborRef());
        target.setTarName(source.getTarName());
        target.setBuildStatus(BaseEnum.fromCode(ClusterImageStatusEnum.class, source.getBuildStatus()));
        target.setBuildRetryCount(source.getBuildRetryCount());
        target.setBuildLogPath(source.getBuildLogPath());
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
        target.setRemark(source.getRemark());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    public static ClusterImageDO toDO(ClusterImage source) {
        if (source == null) {
            return null;
        }
        ClusterImageDO target = new ClusterImageDO();
        target.setId(source.getId());
        target.setImageName(source.getImageName());
        target.setVersion(source.getVersion());
        target.setImageType(ObjectUtil.isNull(source.getImageType()) ? null : source.getImageType().getCode());
        target.setGitUrl(source.getGitUrl());
        target.setGitBranch(source.getGitBranch());
        target.setDockerfile(source.getDockerfile());
        target.setExternalImage(source.getExternalImage());
        target.setHarborRef(source.getHarborRef());
        target.setTarName(source.getTarName());
        target.setBuildStatus(ObjectUtil.isNull(source.getBuildStatus()) ? null : source.getBuildStatus().getCode());
        target.setBuildRetryCount(source.getBuildRetryCount());
        target.setBuildLogPath(source.getBuildLogPath());
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
        target.setRemark(source.getRemark());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    public static ClusterImageDalQuery toDalQuery(ClusterImageQueryParam source) {
        ClusterImageDalQuery target = new ClusterImageDalQuery();
        if (source == null) {
            return target;
        }
        target.setPageNum(source.getPageNum());
        target.setPageSize(source.getPageSize());
        target.setImageName(source.getImageName());
        target.setVersion(source.getVersion());
        target.setImageType(ObjectUtil.isNull(source.getImageType()) ? null : source.getImageType().getCode());
        target.setBuildStatus(ObjectUtil.isNull(source.getBuildStatus()) ? null : source.getBuildStatus().getCode());
        return target;
    }
}
