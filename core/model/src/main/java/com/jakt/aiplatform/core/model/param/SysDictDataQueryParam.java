package com.jakt.aiplatform.core.model.param;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 字典数据查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysDictDataQueryParam extends PageQueryParam {

    /** 主键。 */
    private Long dictCode;

    /** 字典排序。 */
    private Integer dictSort;

    /** 字典标签。 */
    private String dictLabel;

    /** 字典键值。 */
    private String dictValue;

    /** 字典类型。 */
    private String dictType;

    /** 样式属性（其他样式扩展）。 */
    private String cssClass;

    /** 表格回显样式。 */
    private String listClass;

    /** 是否默认（Y是 N否）。 */
    private String isDefault;

    /** 状态（0正常 1停用）。 */
    private String status;

    /** 备注。 */
    private String remark;

    /** 创建时间起。 */
    private LocalDateTime createTimeBegin;

    /** 创建时间止。 */
    private LocalDateTime createTimeEnd;

    /** 更新时间起。 */
    private LocalDateTime updateTimeBegin;

    /** 更新时间止。 */
    private LocalDateTime updateTimeEnd;

}
