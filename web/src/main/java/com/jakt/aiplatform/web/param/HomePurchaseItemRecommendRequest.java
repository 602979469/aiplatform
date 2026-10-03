package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI 推荐候选产品请求 DTO。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HomePurchaseItemRecommendRequest extends BaseRequest {

    /** 大类编码。 */
    @NotBlank(message = "家具大类不能为空")
    private String bigTypeCode;

    /** 小类编码，如 air_conditioner。 */
    @NotBlank(message = "家具类型不能为空")
    private String typeCode;

    /** 用户已填写的预算，可为空。 */
    private String budgetText;

    /** 用户补充说明（户型、品牌偏好等），可为空。 */
    private String remark;

    /** 本次推荐的口味偏好（如「小米的」「要静音的」，可为空，空则按经济型/销量/口碑通用推荐）。 */
    @Size(max = 100, message = "偏好不要超过 100 字")
    private String preference;
}
