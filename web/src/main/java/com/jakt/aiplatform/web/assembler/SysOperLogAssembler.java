package com.jakt.aiplatform.web.assembler;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.web.param.SysOperLogCreateRequest;
import com.jakt.aiplatform.web.param.SysOperLogQueryRequest;
import com.jakt.aiplatform.web.param.SysOperLogUpdateRequest;
import com.jakt.aiplatform.web.result.SysOperLogResponse;
import com.jakt.aiplatform.core.model.domain.SysOperLog;
import com.jakt.aiplatform.core.model.param.SysOperLogQueryParam;

/**
 * 操作日志对象组装器：DTO 与领域模型互转，只存在于 web。
 */
public final class SysOperLogAssembler {

    private SysOperLogAssembler() {
    }

    /**
     * 创建请求 DTO → 领域模型。
     *
     * @param request 创建操作日志请求 DTO
     * @return 操作日志领域模型
     */
    public static SysOperLog toModel(SysOperLogCreateRequest request) {
        SysOperLog sysOperLog = new SysOperLog();
        sysOperLog.setTitle(request.getTitle());
        sysOperLog.setBusinessType(request.getBusinessType());
        sysOperLog.setMethod(request.getMethod());
        sysOperLog.setRequestMethod(request.getRequestMethod());
        sysOperLog.setOperatorType(request.getOperatorType());
        sysOperLog.setOperName(request.getOperName());
        sysOperLog.setDeptName(request.getDeptName());
        sysOperLog.setOperUrl(request.getOperUrl());
        sysOperLog.setOperIp(request.getOperIp());
        sysOperLog.setOperLocation(request.getOperLocation());
        sysOperLog.setOperParam(request.getOperParam());
        sysOperLog.setJsonResult(request.getJsonResult());
        sysOperLog.setStatus(request.getStatus());
        sysOperLog.setErrorMsg(request.getErrorMsg());
        sysOperLog.setOperTime(request.getOperTime());
        sysOperLog.setCostTime(request.getCostTime());
        return sysOperLog;
    }

    /**
     * 更新请求 DTO + 路径 ID → 领域模型。
     *
     * @param request 更新操作日志请求 DTO
     * @param id      路径中的操作日志 ID
     * @return 操作日志领域模型
     */
    public static SysOperLog toModel(SysOperLogUpdateRequest request, Long id) {
        SysOperLog sysOperLog = new SysOperLog();
        sysOperLog.setOperId(id);
        sysOperLog.setTitle(request.getTitle());
        sysOperLog.setBusinessType(request.getBusinessType());
        sysOperLog.setMethod(request.getMethod());
        sysOperLog.setRequestMethod(request.getRequestMethod());
        sysOperLog.setOperatorType(request.getOperatorType());
        sysOperLog.setOperName(request.getOperName());
        sysOperLog.setDeptName(request.getDeptName());
        sysOperLog.setOperUrl(request.getOperUrl());
        sysOperLog.setOperIp(request.getOperIp());
        sysOperLog.setOperLocation(request.getOperLocation());
        sysOperLog.setOperParam(request.getOperParam());
        sysOperLog.setJsonResult(request.getJsonResult());
        sysOperLog.setStatus(request.getStatus());
        sysOperLog.setErrorMsg(request.getErrorMsg());
        sysOperLog.setOperTime(request.getOperTime());
        sysOperLog.setCostTime(request.getCostTime());
        return sysOperLog;
    }

    /**
     * 查询请求 DTO → 查询参数。
     *
     * @param request 操作日志查询请求 DTO
     * @return 操作日志查询参数
     */
    public static SysOperLogQueryParam toQueryParam(SysOperLogQueryRequest request) {
        SysOperLogQueryParam param = new SysOperLogQueryParam();
        param.setOperId(request.getOperId());
        param.setTitle(request.getTitle());
        param.setBusinessType(request.getBusinessType());
        param.setMethod(request.getMethod());
        param.setRequestMethod(request.getRequestMethod());
        param.setOperatorType(request.getOperatorType());
        param.setOperName(request.getOperName());
        param.setDeptName(request.getDeptName());
        param.setOperUrl(request.getOperUrl());
        param.setOperIp(request.getOperIp());
        param.setOperLocation(request.getOperLocation());
        param.setOperParam(request.getOperParam());
        param.setJsonResult(request.getJsonResult());
        param.setStatus(request.getStatus());
        param.setErrorMsg(request.getErrorMsg());
        param.setOperTime(request.getOperTime());
        param.setCostTime(request.getCostTime());
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
     * @param sysOperLog 操作日志领域模型
     * @return 操作日志响应 VO
     */
    public static SysOperLogResponse toResponse(SysOperLog sysOperLog) {
        SysOperLogResponse response = new SysOperLogResponse();
        response.setOperId(sysOperLog.getOperId());
        response.setTitle(sysOperLog.getTitle());
        response.setBusinessType(sysOperLog.getBusinessType());
        response.setMethod(sysOperLog.getMethod());
        response.setRequestMethod(sysOperLog.getRequestMethod());
        response.setOperatorType(sysOperLog.getOperatorType());
        response.setOperName(sysOperLog.getOperName());
        response.setDeptName(sysOperLog.getDeptName());
        response.setOperUrl(sysOperLog.getOperUrl());
        response.setOperIp(sysOperLog.getOperIp());
        response.setOperLocation(sysOperLog.getOperLocation());
        response.setOperParam(sysOperLog.getOperParam());
        response.setJsonResult(sysOperLog.getJsonResult());
        response.setStatus(sysOperLog.getStatus());
        response.setErrorMsg(sysOperLog.getErrorMsg());
        response.setOperTime(sysOperLog.getOperTime());
        response.setCostTime(sysOperLog.getCostTime());
        response.setCreateTime(sysOperLog.getCreateTime());
        response.setUpdateTime(sysOperLog.getUpdateTime());
        return response;
    }
}
