package com.jakt.aiplatform.web.result;

import com.jakt.aiplatform.core.model.enums.MisfirePolicyEnum;
import com.jakt.aiplatform.core.model.enums.ConcurrentEnum;
import com.jakt.aiplatform.core.model.enums.JobStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 定时任务响应 DTO。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SysJobResponse extends BaseResult {
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
    private MisfirePolicyEnum misfirePolicy;

    /** 是否并发执行（0允许 1禁止）。 */
    private ConcurrentEnum concurrent;

    /** 状态（0正常 1暂停）。 */
    private JobStatusEnum status;

    /** 备注信息。 */
    private String remark;

}
