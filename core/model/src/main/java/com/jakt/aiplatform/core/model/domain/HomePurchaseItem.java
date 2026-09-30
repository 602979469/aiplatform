package com.jakt.aiplatform.core.model.domain;

import java.math.BigDecimal;
import java.util.List;

import com.jakt.aiplatform.common.framework.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 家庭装修采购项领域模型。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HomePurchaseItem extends BaseModel {
    /** 主键。 */
    private Long id;

    /** 大类编码（如 hardcover）。 */
    private String bigTypeCode;

    /** 大类名称（如 硬装）。 */
    private String bigTypeName;

    /** 小类编码（如 air_conditioner）。 */
    private String typeCode;

    /** 小类名称（如 空调）。 */
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

    /** 参考图片（最多 {@code HomePurchaseConstant.MAX_IMAGE_COUNT} 张，按 orderNum 升序）。 */
    private List<HomePurchaseItemImage> images;

}
