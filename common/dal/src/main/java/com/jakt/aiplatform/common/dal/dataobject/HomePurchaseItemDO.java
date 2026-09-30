package com.jakt.aiplatform.common.dal.dataobject;

import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 家庭装修采购项 DO对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HomePurchaseItemDO extends BaseDO {
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

}
