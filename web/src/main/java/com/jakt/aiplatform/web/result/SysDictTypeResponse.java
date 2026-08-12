package com.jakt.aiplatform.web.result;

import com.jakt.aiplatform.core.model.enums.DictTypeStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 字典类型响应 DTO。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SysDictTypeResponse extends BaseResult {
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
