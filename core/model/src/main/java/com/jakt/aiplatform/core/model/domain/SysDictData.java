package com.jakt.aiplatform.core.model.domain;

import com.jakt.aiplatform.core.model.enums.IsDefaultEnum;
import com.jakt.aiplatform.core.model.enums.DictDataStatusEnum;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典数据领域模型。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysDictData extends BaseModel {
    /** 主键。 */
    private Long dictCode;

    /** 字典排序。 */
    private Long dictSort;

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
    private IsDefaultEnum isDefault;

    /** 状态（0正常 1停用）。 */
    private DictDataStatusEnum status;

    /** 备注。 */
    private String remark;

}
