package com.jakt.aiplatform.web.result;

import lombok.Data;

import java.util.List;

/**
 * AI 推荐候选产品响应 DTO。
 */
@Data
public class ProductSuggestionResponse {

    /** 品牌 + 型号 / 产品名称。 */
    private String name;

    /** 参考价格区间。 */
    private String priceRange;

    /** 推荐理由。 */
    private String reason;

    /** 亮点标签。 */
    private List<String> highlights;
}
