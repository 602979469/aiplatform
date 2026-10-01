package com.jakt.aiplatform.web.result;

import lombok.Data;

/**
 * 一句话录入响应 DTO：给用户确认的"预期效果"。
 */
@Data
public class PurchaseItemDraftResponse {

    /** 是否识别成功。 */
    private Boolean valid;

    /** 大类编码。 */
    private String bigTypeCode;

    /** 大类名称。 */
    private String bigTypeName;

    /** 小类编码。 */
    private String typeCode;

    /** 小类名称。 */
    private String typeName;

    /** 产品名称（含品牌）。 */
    private String productName;

    /** 预算原文。 */
    private String budgetText;

    /** 数量。 */
    private Integer quantity;
}
