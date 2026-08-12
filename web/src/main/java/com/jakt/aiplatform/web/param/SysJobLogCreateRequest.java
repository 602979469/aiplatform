package com.jakt.aiplatform.web.param;
import java.time.LocalDateTime;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import com.jakt.aiplatform.core.model.enums.JobLogStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 创建定时任务日志请求 DTO。
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysJobLogCreateRequest extends BaseRequest {

    /** 任务名称。 */
    @NotBlank(message = "任务名称不能为空")
    @Size(max = 64, message = "任务名称长度不能超过 64")
    private String jobName;

    /** 任务组名。 */
    @NotBlank(message = "任务组名不能为空")
    @Size(max = 64, message = "任务组名长度不能超过 64")
    private String jobGroup;

    /** 调用目标字符串。 */
    @NotBlank(message = "调用目标字符串不能为空")
    @Size(max = 500, message = "调用目标字符串长度不能超过 500")
    private String invokeTarget;

    /** 日志信息。 */
    @Size(max = 500, message = "日志信息长度不能超过 500")
    private String jobMessage;

    /** 执行状态（0正常 1失败）。 */
    private JobLogStatusEnum status;

    /** 异常信息。 */
    @Size(max = 2000, message = "异常信息长度不能超过 2000")
    private String exceptionInfo;

    /** 执行开始时间。 */
    private LocalDateTime startTime;

    /** 执行结束时间。 */
    private LocalDateTime endTime;

}
