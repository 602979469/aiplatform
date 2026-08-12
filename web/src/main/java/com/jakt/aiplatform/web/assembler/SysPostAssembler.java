package com.jakt.aiplatform.web.assembler;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.web.param.SysPostCreateRequest;
import com.jakt.aiplatform.web.param.SysPostQueryRequest;
import com.jakt.aiplatform.web.param.SysPostUpdateRequest;
import com.jakt.aiplatform.web.result.SysPostResponse;
import com.jakt.aiplatform.core.model.domain.SysPost;
import com.jakt.aiplatform.core.model.param.SysPostQueryParam;

/**
 * 岗位对象组装器：DTO 与领域模型互转，只存在于 web。
 */
public final class SysPostAssembler {

    private SysPostAssembler() {
    }

    /**
     * 创建请求 DTO → 领域模型。
     *
     * @param request 创建岗位请求 DTO
     * @return 岗位领域模型
     */
    public static SysPost toModel(SysPostCreateRequest request) {
        SysPost sysPost = new SysPost();
        sysPost.setPostCode(request.getPostCode());
        sysPost.setPostName(request.getPostName());
        sysPost.setPostSort(request.getPostSort());
        sysPost.setStatus(request.getStatus());
        sysPost.setRemark(request.getRemark());
        return sysPost;
    }

    /**
     * 更新请求 DTO + 路径 ID → 领域模型。
     *
     * @param request 更新岗位请求 DTO
     * @param id      路径中的岗位 ID
     * @return 岗位领域模型
     */
    public static SysPost toModel(SysPostUpdateRequest request, Long id) {
        SysPost sysPost = new SysPost();
        sysPost.setPostId(id);
        sysPost.setPostCode(request.getPostCode());
        sysPost.setPostName(request.getPostName());
        sysPost.setPostSort(request.getPostSort());
        sysPost.setStatus(request.getStatus());
        sysPost.setRemark(request.getRemark());
        return sysPost;
    }

    /**
     * 查询请求 DTO → 查询参数。
     *
     * @param request 岗位查询请求 DTO
     * @return 岗位查询参数
     */
    public static SysPostQueryParam toQueryParam(SysPostQueryRequest request) {
        SysPostQueryParam param = new SysPostQueryParam();
        param.setPostId(request.getPostId());
        param.setPostCode(request.getPostCode());
        param.setPostName(request.getPostName());
        param.setPostSort(request.getPostSort());
        param.setStatus(request.getStatus());
        param.setRemark(request.getRemark());
        param.setCreateTimeBegin(request.getCreateTimeBegin());
        param.setCreateTimeEnd(request.getCreateTimeEnd());
        param.setUpdateTimeBegin(request.getUpdateTimeBegin());
        param.setUpdateTimeEnd(request.getUpdateTimeEnd());
        param.setPageNum(ObjectUtil.defaultIfNull(request.getPageNum(), 1));
        param.setPageSize(ObjectUtil.defaultIfNull(request.getPageSize(), 10));
        return param;
    }

    /**
     * 领域模型 → 响应 VO。
     *
     * @param sysPost 岗位领域模型
     * @return 岗位响应 VO
     */
    public static SysPostResponse toResponse(SysPost sysPost) {
        SysPostResponse response = new SysPostResponse();
        response.setPostId(sysPost.getPostId());
        response.setPostCode(sysPost.getPostCode());
        response.setPostName(sysPost.getPostName());
        response.setPostSort(sysPost.getPostSort());
        response.setStatus(sysPost.getStatus());
        response.setRemark(sysPost.getRemark());
        response.setCreateTime(sysPost.getCreateTime());
        response.setUpdateTime(sysPost.getUpdateTime());
        return response;
    }
}
