package com.jakt.aiplatform.web.assembler;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.web.param.SysJobCreateRequest;
import com.jakt.aiplatform.web.param.SysJobQueryRequest;
import com.jakt.aiplatform.web.param.SysJobUpdateRequest;
import com.jakt.aiplatform.web.result.SysJobResponse;
import com.jakt.aiplatform.core.model.domain.SysJob;
import com.jakt.aiplatform.core.model.param.SysJobQueryParam;

/**
 * 定时任务对象组装器：DTO 与领域模型互转，只存在于 web。
 */
public final class SysJobAssembler {

    private SysJobAssembler() {
    }

    /**
     * 创建请求 DTO → 领域模型。
     *
     * @param request 创建定时任务请求 DTO
     * @return 定时任务领域模型
     */
    public static SysJob toModel(SysJobCreateRequest request) {
        SysJob sysJob = new SysJob();
        sysJob.setJobName(request.getJobName());
        sysJob.setJobGroup(request.getJobGroup());
        sysJob.setInvokeTarget(request.getInvokeTarget());
        sysJob.setCronExpression(request.getCronExpression());
        sysJob.setMisfirePolicy(request.getMisfirePolicy());
        sysJob.setConcurrent(request.getConcurrent());
        sysJob.setStatus(request.getStatus());
        sysJob.setRemark(request.getRemark());
        return sysJob;
    }

    /**
     * 更新请求 DTO + 路径 ID → 领域模型。
     *
     * @param request 更新定时任务请求 DTO
     * @param id      路径中的定时任务 ID
     * @return 定时任务领域模型
     */
    public static SysJob toModel(SysJobUpdateRequest request, Long id) {
        SysJob sysJob = new SysJob();
        sysJob.setJobId(id);
        sysJob.setJobName(request.getJobName());
        sysJob.setJobGroup(request.getJobGroup());
        sysJob.setInvokeTarget(request.getInvokeTarget());
        sysJob.setCronExpression(request.getCronExpression());
        sysJob.setMisfirePolicy(request.getMisfirePolicy());
        sysJob.setConcurrent(request.getConcurrent());
        sysJob.setStatus(request.getStatus());
        sysJob.setRemark(request.getRemark());
        return sysJob;
    }

    /**
     * 查询请求 DTO → 查询参数。
     *
     * @param request 定时任务查询请求 DTO
     * @return 定时任务查询参数
     */
    public static SysJobQueryParam toQueryParam(SysJobQueryRequest request) {
        SysJobQueryParam param = new SysJobQueryParam();
        param.setJobId(request.getJobId());
        param.setJobName(request.getJobName());
        param.setJobGroup(request.getJobGroup());
        param.setInvokeTarget(request.getInvokeTarget());
        param.setCronExpression(request.getCronExpression());
        param.setMisfirePolicy(request.getMisfirePolicy());
        param.setConcurrent(request.getConcurrent());
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
     * @param sysJob 定时任务领域模型
     * @return 定时任务响应 VO
     */
    public static SysJobResponse toResponse(SysJob sysJob) {
        SysJobResponse response = new SysJobResponse();
        response.setJobId(sysJob.getJobId());
        response.setJobName(sysJob.getJobName());
        response.setJobGroup(sysJob.getJobGroup());
        response.setInvokeTarget(sysJob.getInvokeTarget());
        response.setCronExpression(sysJob.getCronExpression());
        response.setMisfirePolicy(sysJob.getMisfirePolicy());
        response.setConcurrent(sysJob.getConcurrent());
        response.setStatus(sysJob.getStatus());
        response.setRemark(sysJob.getRemark());
        response.setCreateTime(sysJob.getCreateTime());
        response.setUpdateTime(sysJob.getUpdateTime());
        return response;
    }
}
