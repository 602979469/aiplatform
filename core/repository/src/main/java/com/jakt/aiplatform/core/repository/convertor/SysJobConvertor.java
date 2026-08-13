package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysJobDO;
import com.jakt.aiplatform.core.model.domain.SysJob;
import com.jakt.aiplatform.core.model.enums.MisfirePolicyEnum;
import com.jakt.aiplatform.core.model.enums.ConcurrentEnum;
import com.jakt.aiplatform.core.model.enums.JobStatusEnum;
import com.jakt.aiplatform.core.model.param.SysJobQueryParam;
import cn.hutool.core.util.ObjectUtil;


/**
 * 定时任务 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class SysJobConvertor {

    private SysJobConvertor() {
    }

    /**
     * 领域模型 → 查询参数（枚举转 code，显式赋值）。
     *
     * @param job 定时任务领域模型
     * @return 定时任务查询参数
     */
    public static SysJobQueryParam toQueryParam(SysJob job) {
        SysJobQueryParam query = new SysJobQueryParam();
        query.setJobId(job.getJobId());
        query.setJobName(job.getJobName());
        query.setJobGroup(job.getJobGroup());
        query.setInvokeTarget(job.getInvokeTarget());
        query.setCronExpression(job.getCronExpression());
        query.setMisfirePolicy(job.getMisfirePolicy() == null ? null : job.getMisfirePolicy().getCode());
        query.setConcurrent(job.getConcurrent() == null ? null : job.getConcurrent().getCode());
        query.setStatus(job.getStatus() == null ? null : job.getStatus().getCode());
        query.setRemark(job.getRemark());
        return query;
    }

    /**
     * 数据对象 → 查询参数（显式赋值，仅拷贝查询相关字段）。
     *
     * @param condition 定时任务数据对象（条件载体）
     * @return 定时任务查询参数
     */
    public static SysJobQueryParam toQueryParam(SysJobDO condition) {
        SysJobQueryParam query = new SysJobQueryParam();
        query.setJobId(condition.getJobId());
        query.setJobName(condition.getJobName());
        query.setJobGroup(condition.getJobGroup());
        query.setInvokeTarget(condition.getInvokeTarget());
        query.setCronExpression(condition.getCronExpression());
        query.setMisfirePolicy(condition.getMisfirePolicy());
        query.setConcurrent(condition.getConcurrent());
        query.setStatus(condition.getStatus());
        query.setRemark(condition.getRemark());
        return query;
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
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
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
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
