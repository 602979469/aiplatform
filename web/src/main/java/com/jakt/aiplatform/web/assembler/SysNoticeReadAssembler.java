package com.jakt.aiplatform.web.assembler;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.web.param.SysNoticeReadCreateRequest;
import com.jakt.aiplatform.web.param.SysNoticeReadQueryRequest;
import com.jakt.aiplatform.web.param.SysNoticeReadUpdateRequest;
import com.jakt.aiplatform.web.result.SysNoticeReadResponse;
import com.jakt.aiplatform.core.model.domain.SysNoticeRead;
import com.jakt.aiplatform.core.model.param.SysNoticeReadQueryParam;

/**
 * 公告已读记录对象组装器：DTO 与领域模型互转，只存在于 web。
 */
public final class SysNoticeReadAssembler {

    private SysNoticeReadAssembler() {
    }

    /**
     * 创建请求 DTO → 领域模型。
     *
     * @param request 创建公告已读记录请求 DTO
     * @return 公告已读记录领域模型
     */
    public static SysNoticeRead toModel(SysNoticeReadCreateRequest request) {
        SysNoticeRead sysNoticeRead = new SysNoticeRead();
        sysNoticeRead.setNoticeId(request.getNoticeId());
        sysNoticeRead.setUserId(request.getUserId());
        sysNoticeRead.setReadTime(request.getReadTime());
        return sysNoticeRead;
    }

    /**
     * 更新请求 DTO + 路径 ID → 领域模型。
     *
     * @param request 更新公告已读记录请求 DTO
     * @param id      路径中的公告已读记录 ID
     * @return 公告已读记录领域模型
     */
    public static SysNoticeRead toModel(SysNoticeReadUpdateRequest request, Long id) {
        SysNoticeRead sysNoticeRead = new SysNoticeRead();
        sysNoticeRead.setReadId(id);
        sysNoticeRead.setNoticeId(request.getNoticeId());
        sysNoticeRead.setUserId(request.getUserId());
        sysNoticeRead.setReadTime(request.getReadTime());
        return sysNoticeRead;
    }

    /**
     * 查询请求 DTO → 查询参数。
     *
     * @param request 公告已读记录查询请求 DTO
     * @return 公告已读记录查询参数
     */
    public static SysNoticeReadQueryParam toQueryParam(SysNoticeReadQueryRequest request) {
        SysNoticeReadQueryParam param = new SysNoticeReadQueryParam();
        param.setReadId(request.getReadId());
        param.setNoticeId(request.getNoticeId());
        param.setUserId(request.getUserId());
        param.setReadTime(request.getReadTime());
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
     * @param sysNoticeRead 公告已读记录领域模型
     * @return 公告已读记录响应 VO
     */
    public static SysNoticeReadResponse toResponse(SysNoticeRead sysNoticeRead) {
        SysNoticeReadResponse response = new SysNoticeReadResponse();
        response.setReadId(sysNoticeRead.getReadId());
        response.setNoticeId(sysNoticeRead.getNoticeId());
        response.setUserId(sysNoticeRead.getUserId());
        response.setReadTime(sysNoticeRead.getReadTime());
        response.setCreateTime(sysNoticeRead.getCreateTime());
        response.setUpdateTime(sysNoticeRead.getUpdateTime());
        return response;
    }
}
