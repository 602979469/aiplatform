package com.jakt.aiplatform.web.assembler;

import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.core.model.domain.ClusterImage;
import com.jakt.aiplatform.core.model.param.ClusterImageQueryParam;
import com.jakt.aiplatform.web.param.ClusterImageCreateRequest;
import com.jakt.aiplatform.web.param.ClusterImageQueryRequest;
import com.jakt.aiplatform.web.param.ClusterImageUpdateRequest;
import com.jakt.aiplatform.web.result.ClusterImageResponse;

import java.util.List;

/**
 * 镜像域组装器：DTO ↔ Model、响应转换。
 */
public final class ClusterImageAssembler {

    private ClusterImageAssembler() {
    }

    public static ClusterImageResponse toResponse(ClusterImage image) {
        if (image == null) {
            return null;
        }
        ClusterImageResponse response = new ClusterImageResponse();
        response.setId(image.getId());
        response.setImageName(image.getImageName());
        response.setVersion(image.getVersion());
        response.setImageType(image.getImageType());
        response.setGitUrl(image.getGitUrl());
        response.setGitBranch(image.getGitBranch());
        response.setDockerfile(image.getDockerfile());
        response.setExternalImage(image.getExternalImage());
        response.setHarborRef(image.getHarborRef());
        response.setTarName(image.getTarName());
        response.setBuildStatus(image.getBuildStatus());
        response.setBuildRetryCount(image.getBuildRetryCount());
        response.setBuildLogPath(image.getBuildLogPath());
        response.setRemark(image.getRemark());
        response.setCreateTime(image.getCreateTime());
        response.setUpdateTime(image.getUpdateTime());
        return response;
    }

    public static List<ClusterImageResponse> toResponseList(List<ClusterImage> list) {
        return ConvertUtil.map(list, ClusterImageAssembler::toResponse);
    }

    public static ClusterImage toModel(ClusterImageCreateRequest request) {
        ClusterImage image = new ClusterImage();
        image.setImageName(request.getImageName());
        image.setVersion(request.getVersion());
        image.setImageType(request.getImageType());
        image.setGitUrl(request.getGitUrl());
        image.setGitBranch(request.getGitBranch());
        image.setDockerfile(request.getDockerfile());
        image.setExternalImage(request.getExternalImage());
        image.setRemark(request.getRemark());
        return image;
    }

    public static ClusterImage toModel(ClusterImageUpdateRequest request, Long id) {
        ClusterImage image = toModel(new ClusterImageCreateRequest());
        image.setId(id);
        image.setImageName(request.getImageName());
        image.setVersion(request.getVersion());
        image.setImageType(request.getImageType());
        image.setGitUrl(request.getGitUrl());
        image.setGitBranch(request.getGitBranch());
        image.setDockerfile(request.getDockerfile());
        image.setExternalImage(request.getExternalImage());
        image.setRemark(request.getRemark());
        return image;
    }

    public static ClusterImageQueryParam toQueryParam(ClusterImageQueryRequest request) {
        ClusterImageQueryParam query = new ClusterImageQueryParam();
        if (request == null) {
            return query;
        }
        query.setImageName(request.getImageName());
        query.setVersion(request.getVersion());
        query.setImageType(request.getImageType());
        query.setBuildStatus(request.getBuildStatus());
        query.setPageNum(request.getPageNum());
        query.setPageSize(request.getPageSize());
        return query;
    }
}
