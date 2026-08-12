package com.jakt.aiplatform.web.assembler;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.web.param.SysJobLogCreateRequest;
import com.jakt.aiplatform.web.param.SysJobLogQueryRequest;
import com.jakt.aiplatform.web.param.SysJobLogUpdateRequest;
import com.jakt.aiplatform.web.result.SysJobLogResponse;
import com.jakt.aiplatform.core.model.domain.SysJobLog;
import com.jakt.aiplatform.core.model.param.SysJobLogQueryParam;

/**
 * 定时任务日志对象组装器：DTO 与领域模型互转，只存在于 web。
 */
public final class SysJobLogAssembler {

    private SysJobLogAssembler() {
    }

    /**
     * 创建请求 DTO → 领域模型。
     *
     * @param request 创建定时任务日志请求 DTO
     * @return 定时任务日志领域模型
     */
    public static SysJobLog toModel(SysJobLogCreateRequest request) {
        SysJobLog sysJobLog = new SysJobLog();
        sysJobLog.setJobName(request.getJobName());
        sysJobLog.setJobGroup(request.getJobGroup());
        sysJobLog.setInvokeTarget(request.getInvokeTarget());
        sysJobLog.setJobMessage(request.getJobMessage());
        sysJobLog.setStatus(request.getStatus());
        sysJobLog.setExceptionInfo(request.getExceptionInfo());
        sysJobLog.setStartTime(request.getStartTime());
        sysJobLog.setEndTime(request.getEndTime());
        return sysJobLog;
    }

    /**
     * 更新请求 DTO + 路径 ID → 领域模型。
     *
     * @param request 更新定时任务日志请求 DTO
     * @param id      路径中的定时任务日志 ID
     * @return 定时任务日志领域模型
     */
    public static SysJobLog toModel(SysJobLogUpdateRequest request, Long id) {
        SysJobLog sysJobLog = new SysJobLog();
        sysJobLog.setJobLogId(id);
        sysJobLog.setJobName(request.getJobName());
        sysJobLog.setJobGroup(request.getJobGroup());
        sysJobLog.setInvokeTarget(request.getInvokeTarget());
        sysJobLog.setJobMessage(request.getJobMessage());
        sysJobLog.setStatus(request.getStatus());
        sysJobLog.setExceptionInfo(request.getExceptionInfo());
        sysJobLog.setStartTime(request.getStartTime());
        sysJobLog.setEndTime(request.getEndTime());
        return sysJobLog;
    }

    /**
     * 查询请求 DTO → 查询参数。
     *
     * @param request 定时任务日志查询请求 DTO
     * @return 定时任务日志查询参数
     */
    public static SysJobLogQueryParam toQueryParam(SysJobLogQueryRequest request) {
        SysJobLogQueryParam param = new SysJobLogQueryParam();
        param.setJobLogId(request.getJobLogId());
        param.setJobName(request.getJobName());
        param.setJobGroup(request.getJobGroup());
        param.setInvokeTarget(request.getInvokeTarget());
        param.setJobMessage(request.getJobMessage());
        param.setStatus(request.getStatus());
        param.setExceptionInfo(request.getExceptionInfo());
        param.setStartTime(request.getStartTime());
        param.setEndTime(request.getEndTime());
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
     * @param sysJobLog 定时任务日志领域模型
     * @return 定时任务日志响应 VO
     */
    public static SysJobLogResponse toResponse(SysJobLog sysJobLog) {
        SysJobLogResponse response = new SysJobLogResponse();
        response.setJobLogId(sysJobLog.getJobLogId());
        response.setJobName(sysJobLog.getJobName());
        response.setJobGroup(sysJobLog.getJobGroup());
        response.setInvokeTarget(sysJobLog.getInvokeTarget());
        response.setJobMessage(sysJobLog.getJobMessage());
        response.setStatus(sysJobLog.getStatus());
        response.setExceptionInfo(sysJobLog.getExceptionInfo());
        response.setStartTime(sysJobLog.getStartTime());
        response.setEndTime(sysJobLog.getEndTime());
        response.setCreateTime(sysJobLog.getCreateTime());
        response.setUpdateTime(sysJobLog.getUpdateTime());
        return response;
    }
}
