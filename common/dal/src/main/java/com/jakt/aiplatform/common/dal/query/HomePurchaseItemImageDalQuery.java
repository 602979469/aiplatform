package com.jakt.aiplatform.common.dal.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;
/**
 * 家庭装修采购项图片查询参数（common-dal 专用）：字段为数据库原始类型，仅供 Mapper/XML 使用。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HomePurchaseItemImageDalQuery extends DalPageQuery {

    /** 主键。 */
    private Long id;

    /** 采购项ID（home_purchase_item.id）。 */
    private Long itemId;

    /** 采购项ID集合：列表页一次性取回多行图片，避免逐行查询。 */
    private List<Long> itemIds;

    /** 文件ID（file_info.id）。 */
    private Long fileId;

    /** 排序（0 起，最多 10 张）。 */
    private Integer orderNum;

    /** 创建时间起。 */
    private LocalDateTime createTimeBegin;

    /** 创建时间止。 */
    private LocalDateTime createTimeEnd;

    /** 更新时间起。 */
    private LocalDateTime updateTimeBegin;

    /** 更新时间止。 */
    private LocalDateTime updateTimeEnd;

}
