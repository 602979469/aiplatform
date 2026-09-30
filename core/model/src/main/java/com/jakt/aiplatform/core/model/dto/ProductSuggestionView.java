package com.jakt.aiplatform.core.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * AI 推荐的单款产品（模型返回 JSON 反序列化目标）。
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductSuggestionView {

    /** 品牌 + 型号 / 产品名称。 */
    private String name;

    /** 参考价格区间，如 2999~3599。 */
    private String priceRange;

    /** 推荐理由（经济性、销量、口碑）。 */
    private String reason;

    /** 亮点标签。 */
    private List<String> highlights;
}
