package com.jakt.aiplatform.web.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 家庭装修采购项响应 DTO。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomePurchaseItemResponse extends BaseResult {
    /** 主键。 */
    private Long id;

    /** 大类编码。 */
    private String bigTypeCode;

    /** 大类名称快照。 */
    private String bigTypeName;

    /** 小类编码。 */
    private String typeCode;

    /** 小类名称快照。 */
    private String typeName;

    /** 产品名称。 */
    private String productName;

    /** 采购数量。 */
    private Integer quantity;

    /** 预算原文（800~1200 或 999）。 */
    private String budgetText;

    /** 预算下限（单件）。 */
    private BigDecimal budgetMin;

    /** 预算上限（单件）。 */
    private BigDecimal budgetMax;

    /** 安装费（单件）。 */
    private BigDecimal installFee;

    /** 备注。 */
    private String remark;

    /** 录入人（auth_user.user_id）。 */
    private Long userId;

    /** 参考图片（最多 10 张，顺序即展示顺序）。 */
    private List<HomePurchaseItemImageResponse> images;
}
