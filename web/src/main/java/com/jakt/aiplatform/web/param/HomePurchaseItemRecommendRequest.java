package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.NotBlank;
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
}
