package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysJobLogDO;
import com.jakt.aiplatform.core.model.domain.SysJobLog;
import com.jakt.aiplatform.core.model.enums.JobLogStatusEnum;
import com.jakt.aiplatform.core.model.param.SysJobLogQueryParam;
import cn.hutool.core.util.ObjectUtil;


/**
 * 定时任务日志 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class SysJobLogConvertor {

    private SysJobLogConvertor() {
    }

    /**
     * 领域模型 → 查询参数（枚举转 code，显式赋值）。
     *
     * @param jobLog 定时任务日志领域模型
     * @return 定时任务日志查询参数
     */
    public static SysJobLogQueryParam toQueryParam(SysJobLog jobLog) {
        SysJobLogQueryParam query = new SysJobLogQueryParam();
        query.setJobLogId(jobLog.getJobLogId());
        query.setJobName(jobLog.getJobName());
        query.setJobGroup(jobLog.getJobGroup());
        query.setInvokeTarget(jobLog.getInvokeTarget());
        query.setJobMessage(jobLog.getJobMessage());
        query.setStatus(jobLog.getStatus() == null ? null : jobLog.getStatus().getCode());
        query.setExceptionInfo(jobLog.getExceptionInfo());
        query.setStartTime(jobLog.getStartTime());
        query.setEndTime(jobLog.getEndTime());
        return query;
    }

    /**
     * 数据对象 → 查询参数（显式赋值，仅拷贝查询相关字段）。
     *
     * @param condition 定时任务日志数据对象（条件载体）
     * @return 定时任务日志查询参数
     */
    public static SysJobLogQueryParam toQueryParam(SysJobLogDO condition) {
        SysJobLogQueryParam query = new SysJobLogQueryParam();
        query.setJobLogId(condition.getJobLogId());
        query.setJobName(condition.getJobName());
        query.setJobGroup(condition.getJobGroup());
        query.setInvokeTarget(condition.getInvokeTarget());
        query.setJobMessage(condition.getJobMessage());
        query.setStatus(condition.getStatus());
        query.setExceptionInfo(condition.getExceptionInfo());
        query.setStartTime(condition.getStartTime());
        query.setEndTime(condition.getEndTime());
        return query;
    }

    /**
     * DO → 领域模型。
     *
     * @param sysJobLogDO 定时任务日志数据对象；为空返回 null
     * @return 定时任务日志领域模型
     */
    public static SysJobLog toModel(SysJobLogDO source) {
        if (source == null) {
            return null;
        }
        SysJobLog target = new SysJobLog();
        target.setJobLogId(source.getJobLogId());
        target.setJobName(source.getJobName());
        target.setJobGroup(source.getJobGroup());
        target.setInvokeTarget(source.getInvokeTarget());
        target.setJobMessage(source.getJobMessage());
        target.setStatus(JobLogStatusEnum.fromCode(source.getStatus()));
        target.setExceptionInfo(source.getExceptionInfo());
        target.setStartTime(source.getStartTime());
        target.setEndTime(source.getEndTime());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param sysJobLog 定时任务日志领域模型
     * @return 定时任务日志数据对象
     */
    public static SysJobLogDO toDO(SysJobLog source) {
        SysJobLogDO target = new SysJobLogDO();
        target.setJobLogId(source.getJobLogId());
        target.setJobName(source.getJobName());
        target.setJobGroup(source.getJobGroup());
        target.setInvokeTarget(source.getInvokeTarget());
        target.setJobMessage(source.getJobMessage());
        target.setStatus(ObjectUtil.isNull(source.getStatus()) ? null : source.getStatus().getCode());
        target.setExceptionInfo(source.getExceptionInfo());
        target.setStartTime(source.getStartTime());
        target.setEndTime(source.getEndTime());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
