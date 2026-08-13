package com.jakt.aiplatform.core.model.domain;

import java.time.LocalDateTime;
import com.jakt.aiplatform.core.model.enums.JobLogStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 定时任务日志领域模型（RuoYi 结构：继承 BaseEntity）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysJobLog extends BaseEntity {

    /** 任务日志ID。 */
    private Long jobLogId;

    /** 任务名称。 */
    private String jobName;

    /** 任务组名。 */
    private String jobGroup;

    /** 调用目标字符串。 */
    private String invokeTarget;

    /** 日志信息。 */
    private String jobMessage;

    /** 执行状态（0正常 1失败）。 */
    private JobLogStatusEnum status;

    /** 异常信息。 */
    private String exceptionInfo;

    /** 执行开始时间。 */
    private LocalDateTime startTime;

    /** 执行结束时间。 */
    private LocalDateTime endTime;
}
