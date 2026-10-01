package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 一句话录入请求 DTO：用户口语原话。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HomePurchaseItemParseRequest extends BaseRequest {

    /** 用户原话，如「我需要添加一个软装-阳台休闲椅，预算3000，小米的」。 */
    @NotBlank(message = "说一句要添加什么吧")
    @Size(max = 200, message = "一句话不要超过 200 字")
    private String text;
}
