package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysJobLogDO;
import com.jakt.aiplatform.core.model.domain.SysJobLog;
import com.jakt.aiplatform.core.model.enums.JobLogStatusEnum;
import cn.hutool.core.util.ObjectUtil;


/**
 * 定时任务日志 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class SysJobLogConvertor {

    private SysJobLogConvertor() {
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
