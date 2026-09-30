package com.jakt.aiplatform.common.dal.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.math.BigDecimal;
/**
 * 家庭装修采购项查询参数（common-dal 专用）：字段为数据库原始类型，仅供 Mapper/XML 使用。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HomePurchaseItemDalQuery extends DalPageQuery {

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

    /** 创建时间起。 */
    private LocalDateTime createTimeBegin;

    /** 创建时间止。 */
    private LocalDateTime createTimeEnd;

    /** 更新时间起。 */
    private LocalDateTime updateTimeBegin;

    /** 更新时间止。 */
    private LocalDateTime updateTimeEnd;

}
