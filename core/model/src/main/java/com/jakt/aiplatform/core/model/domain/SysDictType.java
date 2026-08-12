package com.jakt.aiplatform.core.model.domain;

import com.jakt.aiplatform.core.model.enums.DictTypeStatusEnum;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典类型领域模型。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysDictType extends BaseModel {
    /** 主键。 */
    private Long dictId;

    /** 字典名称。 */
    private String dictName;

    /** 字典类型。 */
    private String dictType;

    /** 状态（0正常 1停用）。 */
    private DictTypeStatusEnum status;

    /** 备注。 */
    private String remark;

}
