package com.jakt.aiplatform.core.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 一句话录入的解析草稿：既承接模型返回的 JSON，也承载服务端校验后的字段（未落库，供用户确认）。
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseItemDraftView {

    /** 模型是否识别为一次有效的采购项录入。 */
    private Boolean valid;

    /** 无效原因（短句，直接展示给用户）。 */
    private String reason;

    /** 大类编码（模型给，服务端校验）。 */
    private String bigTypeCode;

    /** 大类名称（服务端按配置回填）。 */
    private String bigTypeName;

    /** 小类编码（模型给，服务端校验）。 */
    private String typeCode;

    /** 小类名称（服务端按配置回填）。 */
    private String typeName;

    /** 产品名称（含品牌，如 小米 阳台休闲椅）。 */
    private String productName;

    /** 预算原文（用户怎么写就怎么存）。 */
    private String budgetText;

    /** 数量。 */
    private Integer quantity;
}
