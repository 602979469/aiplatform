package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ES 同步任务操作请求（删除映射 / 触发全量导入）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CdcTaskRequest extends BaseRequest {

    /** 任务名（可带 .yml 后缀）。 */
    @NotBlank(message = "任务名不能为空")
    @Size(max = 64, message = "任务名长度不能超过 64")
    private String name;

    /** 是否顺带滚动重启适配器（null 视为重启，仅删除时使用）。 */
    private Boolean restart;
}
