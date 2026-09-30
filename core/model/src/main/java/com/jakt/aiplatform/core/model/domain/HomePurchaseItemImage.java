package com.jakt.aiplatform.core.model.domain;


import com.jakt.aiplatform.common.framework.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 家庭装修采购项图片领域模型。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HomePurchaseItemImage extends BaseModel {
    /** 主键。 */
    private Long id;

    /** 采购项ID（home_purchase_item.id）。 */
    private Long itemId;

    /** 文件ID（file_info.id）。 */
    private Long fileId;

    /** 排序（0 起，最多 10 张）。 */
    private Integer orderNum;

}
