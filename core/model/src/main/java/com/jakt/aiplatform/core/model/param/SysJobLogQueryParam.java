package com.jakt.aiplatform.core.model.param;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 定时任务日志查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysJobLogQueryParam extends PageQueryParam {

    /** 主键。 */
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
    private String status;

    /** 异常信息。 */
    private String exceptionInfo;

    /** 执行开始时间。 */
    private LocalDateTime startTime;

    /** 执行结束时间。 */
    private LocalDateTime endTime;

    /** 创建时间起。 */
    private LocalDateTime createTimeBegin;

    /** 创建时间止。 */
    private LocalDateTime createTimeEnd;

    /** 更新时间起。 */
    private LocalDateTime updateTimeBegin;

    /** 更新时间止。 */
    private LocalDateTime updateTimeEnd;

}
