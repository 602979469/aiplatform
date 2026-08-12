package com.jakt.aiplatform.web.result;

import java.time.LocalDateTime;
import com.jakt.aiplatform.core.model.enums.JobLogStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 定时任务日志响应 DTO。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SysJobLogResponse extends BaseResult {
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
    private JobLogStatusEnum status;

    /** 异常信息。 */
    private String exceptionInfo;

    /** 执行开始时间。 */
    private LocalDateTime startTime;

    /** 执行结束时间。 */
    private LocalDateTime endTime;

}
