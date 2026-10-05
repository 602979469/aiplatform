package com.jakt.aiplatform.core.model.param;

import lombok.Data;

/**
 * 产品推荐入参：类型（必填）+ 可选的目标产品关键词 + 预算 + 补充说明 + 本次偏好。
 */
@Data
public class ProductRecommendParam {

    /** 大类编码。 */
    private String bigTypeCode;

    /** 小类编码。 */
    private String typeCode;

    /** 目标产品/关键词（用户点名要的品类或型号，如「海尔法式双开门冰箱」），可为空。 */
    private String productName;

    /** 预算原文，可为空。 */
    private String budgetText;

    /** 补充说明，可为空。 */
    private String remark;

    /** 本次偏好（如「小米的」「要便宜的」），可为空。 */
    private String preference;
}
