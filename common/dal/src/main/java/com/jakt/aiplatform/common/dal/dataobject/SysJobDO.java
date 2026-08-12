package com.jakt.aiplatform.common.dal.dataobject;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 定时任务 DO对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysJobDO extends BaseDO {
    /** 主键。 */
    private Long jobId;

    /** 任务名称。 */
    private String jobName;

    /** 任务组名。 */
    private String jobGroup;

    /** 调用目标字符串。 */
    private String invokeTarget;

    /** cron执行表达式。 */
    private String cronExpression;

    /** 计划执行错误策略（1立即执行 2执行一次 3放弃执行）。 */
    private String misfirePolicy;

    /** 是否并发执行（0允许 1禁止）。 */
    private String concurrent;

    /** 状态（0正常 1暂停）。 */
    private String status;

    /** 备注信息。 */
    private String remark;

}
