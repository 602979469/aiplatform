package com.jakt.aiplatform.web.param;

import java.time.LocalDateTime;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 定时任务日志查询请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysJobLogQueryRequest extends BaseRequest {

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

    /** 页码，从 1 开始。 */
    @Min(value = 1, message = "页码不能小于 1")
    private Integer pageNum = 1;

    /** 每页条数。 */
    @Min(value = 1, message = "每页条数不能小于 1")
    @Max(value = 100, message = "每页条数不能超过 100")
    private Integer pageSize = 10;
}
