package com.jakt.aiplatform.web.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 家庭装修采购项查询请求。分页参数由 {@link PageQueryRequest} 提供。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HomePurchaseItemQueryRequest extends PageQueryRequest {

    /** 主键。 */
    private Long id;

    /** 大类编码。 */
    private String bigTypeCode;

    /** 小类编码。 */
    private String typeCode;

    /** 产品名称（模糊匹配）。 */
    private String productName;

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
