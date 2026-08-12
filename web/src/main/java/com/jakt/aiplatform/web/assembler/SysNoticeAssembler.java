package com.jakt.aiplatform.web.assembler;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.web.param.SysNoticeCreateRequest;
import com.jakt.aiplatform.web.param.SysNoticeQueryRequest;
import com.jakt.aiplatform.web.param.SysNoticeUpdateRequest;
import com.jakt.aiplatform.web.result.SysNoticeResponse;
import com.jakt.aiplatform.core.model.domain.SysNotice;
import com.jakt.aiplatform.core.model.param.SysNoticeQueryParam;

/**
 * 通知公告对象组装器：DTO 与领域模型互转，只存在于 web。
 */
public final class SysNoticeAssembler {

    private SysNoticeAssembler() {
    }

    /**
     * 创建请求 DTO → 领域模型。
     *
     * @param request 创建通知公告请求 DTO
     * @return 通知公告领域模型
     */
    public static SysNotice toModel(SysNoticeCreateRequest request) {
        SysNotice sysNotice = new SysNotice();
        sysNotice.setNoticeTitle(request.getNoticeTitle());
        sysNotice.setNoticeType(request.getNoticeType());
        sysNotice.setNoticeContent(request.getNoticeContent());
        sysNotice.setStatus(request.getStatus());
        sysNotice.setRemark(request.getRemark());
        return sysNotice;
    }

    /**
     * 更新请求 DTO + 路径 ID → 领域模型。
     *
     * @param request 更新通知公告请求 DTO
     * @param id      路径中的通知公告 ID
     * @return 通知公告领域模型
     */
    public static SysNotice toModel(SysNoticeUpdateRequest request, Long id) {
        SysNotice sysNotice = new SysNotice();
        sysNotice.setNoticeId(id);
        sysNotice.setNoticeTitle(request.getNoticeTitle());
        sysNotice.setNoticeType(request.getNoticeType());
        sysNotice.setNoticeContent(request.getNoticeContent());
        sysNotice.setStatus(request.getStatus());
        sysNotice.setRemark(request.getRemark());
        return sysNotice;
    }

    /**
     * 查询请求 DTO → 查询参数。
     *
     * @param request 通知公告查询请求 DTO
     * @return 通知公告查询参数
     */
    public static SysNoticeQueryParam toQueryParam(SysNoticeQueryRequest request) {
        SysNoticeQueryParam param = new SysNoticeQueryParam();
        param.setNoticeId(request.getNoticeId());
        param.setNoticeTitle(request.getNoticeTitle());
        param.setNoticeType(request.getNoticeType());
        param.setNoticeContent(request.getNoticeContent());
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
     * @param sysNotice 通知公告领域模型
     * @return 通知公告响应 VO
     */
    public static SysNoticeResponse toResponse(SysNotice sysNotice) {
        SysNoticeResponse response = new SysNoticeResponse();
        response.setNoticeId(sysNotice.getNoticeId());
        response.setNoticeTitle(sysNotice.getNoticeTitle());
        response.setNoticeType(sysNotice.getNoticeType());
        response.setNoticeContent(sysNotice.getNoticeContent());
        response.setStatus(sysNotice.getStatus());
        response.setRemark(sysNotice.getRemark());
        response.setCreateTime(sysNotice.getCreateTime());
        response.setUpdateTime(sysNotice.getUpdateTime());
        return response;
    }
}
