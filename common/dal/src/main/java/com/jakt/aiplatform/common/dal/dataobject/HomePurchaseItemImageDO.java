package com.jakt.aiplatform.common.dal.dataobject;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 家庭装修采购项图片 DO对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HomePurchaseItemImageDO extends BaseDO {
    /** 主键。 */
    private Long id;

    /** 采购项ID（home_purchase_item.id）。 */
    private Long itemId;

    /** 文件ID（file_info.id）。 */
    private Long fileId;

    /** 排序（0 起，最多 10 张）。 */
    private Integer orderNum;

}
