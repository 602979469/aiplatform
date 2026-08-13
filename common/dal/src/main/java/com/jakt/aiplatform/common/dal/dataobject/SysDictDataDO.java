package com.jakt.aiplatform.common.dal.dataobject;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典数据 DO对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysDictDataDO extends BaseDO {
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

    /** 创建者。 */
    private String createBy;

    /** 更新者。 */
    private String updateBy;
}
