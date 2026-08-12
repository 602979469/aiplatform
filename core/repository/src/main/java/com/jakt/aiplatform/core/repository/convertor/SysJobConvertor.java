package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysJobDO;
import com.jakt.aiplatform.core.model.domain.SysJob;
import com.jakt.aiplatform.core.model.enums.MisfirePolicyEnum;
import com.jakt.aiplatform.core.model.enums.ConcurrentEnum;
import com.jakt.aiplatform.core.model.enums.JobStatusEnum;
import cn.hutool.core.util.ObjectUtil;


/**
 * 定时任务 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class SysJobConvertor {

    private SysJobConvertor() {
    }

    /**
     * DO → 领域模型。
     *
     * @param sysJobDO 定时任务数据对象；为空返回 null
     * @return 定时任务领域模型
     */
    public static SysJob toModel(SysJobDO source) {
        if (source == null) {
            return null;
        }
        SysJob target = new SysJob();
        target.setJobId(source.getJobId());
        target.setJobName(source.getJobName());
        target.setJobGroup(source.getJobGroup());
        target.setInvokeTarget(source.getInvokeTarget());
        target.setCronExpression(source.getCronExpression());
        target.setMisfirePolicy(MisfirePolicyEnum.fromCode(source.getMisfirePolicy()));
        target.setConcurrent(ConcurrentEnum.fromCode(source.getConcurrent()));
        target.setStatus(JobStatusEnum.fromCode(source.getStatus()));
        target.setRemark(source.getRemark());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param sysJob 定时任务领域模型
     * @return 定时任务数据对象
     */
    public static SysJobDO toDO(SysJob source) {
        SysJobDO target = new SysJobDO();
        target.setJobId(source.getJobId());
        target.setJobName(source.getJobName());
        target.setJobGroup(source.getJobGroup());
        target.setInvokeTarget(source.getInvokeTarget());
        target.setCronExpression(source.getCronExpression());
        target.setMisfirePolicy(ObjectUtil.isNull(source.getMisfirePolicy()) ? null : source.getMisfirePolicy().getCode());
        target.setConcurrent(ObjectUtil.isNull(source.getConcurrent()) ? null : source.getConcurrent().getCode());
        target.setStatus(ObjectUtil.isNull(source.getStatus()) ? null : source.getStatus().getCode());
        target.setRemark(source.getRemark());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
