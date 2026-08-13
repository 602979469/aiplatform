package com.jakt.aiplatform.common.dal.dataobject;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典类型 DO对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysDictTypeDO extends BaseDO {
    /** 主键。 */
    private Long dictId;

    /** 字典名称。 */
    private String dictName;

    /** 字典类型。 */
    private String dictType;

    /** 状态（0正常 1停用）。 */
    private String status;

    /** 备注。 */
    private String remark;

    /** 创建者。 */
    private String createBy;

    /** 更新者。 */
    private String updateBy;
}
