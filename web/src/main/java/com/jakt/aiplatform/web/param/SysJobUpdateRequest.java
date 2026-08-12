package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import com.jakt.aiplatform.core.model.enums.MisfirePolicyEnum;
import com.jakt.aiplatform.core.model.enums.ConcurrentEnum;
import com.jakt.aiplatform.core.model.enums.JobStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 更新定时任务请求 DTO。
 *
 * <p>校验规则与 sys_job 表字段对齐：非空 + varchar 长度，不做业务自定义规则。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysJobUpdateRequest extends BaseRequest {
    /** 任务名称。 */
    @Size(max = 64, message = "任务名称长度不能超过 64")
    private String jobName;

    /** 任务组名。 */
    @Size(max = 64, message = "任务组名长度不能超过 64")
    private String jobGroup;

    /** 调用目标字符串。 */
    @NotBlank(message = "调用目标字符串不能为空")
    @Size(max = 500, message = "调用目标字符串长度不能超过 500")
    private String invokeTarget;

    /** cron执行表达式。 */
    @Size(max = 255, message = "cron执行表达式长度不能超过 255")
    private String cronExpression;

    /** 计划执行错误策略（1立即执行 2执行一次 3放弃执行）。 */
    private MisfirePolicyEnum misfirePolicy;

    /** 是否并发执行（0允许 1禁止）。 */
    private ConcurrentEnum concurrent;

    /** 状态（0正常 1暂停）。 */
    private JobStatusEnum status;

    /** 备注信息。 */
    @Size(max = 500, message = "备注信息长度不能超过 500")
    private String remark;

}
