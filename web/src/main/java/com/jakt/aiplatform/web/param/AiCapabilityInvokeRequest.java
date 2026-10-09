package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI 能力调用请求：按场景码 + 能力码调用（自检/调试用）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiCapabilityInvokeRequest extends BaseRequest {

    /** 场景码。 */
    @NotBlank(message = "场景码不能为空")
    @Size(max = 64, message = "场景码长度不能超过 64")
    private String sceneCode;

    /** 能力码。 */
    @NotBlank(message = "能力码不能为空")
    @Size(max = 64, message = "能力码长度不能超过 64")
    private String capabilityCode;

    /** 能力入参。 */
    @NotBlank(message = "入参不能为空")
    @Size(max = 4000, message = "入参长度不能超过 4000")
    private String input;
}
