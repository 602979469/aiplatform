package com.jakt.aiplatform.core.model.param;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 定时任务查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysJobQueryParam extends PageParam {

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

    /** 创建时间起。 */
    private LocalDateTime createTimeBegin;

    /** 创建时间止。 */
    private LocalDateTime createTimeEnd;

    /** 更新时间起。 */
    private LocalDateTime updateTimeBegin;

    /** 更新时间止。 */
    private LocalDateTime updateTimeEnd;

}
