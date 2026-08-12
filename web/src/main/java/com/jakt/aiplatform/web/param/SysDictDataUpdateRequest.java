package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.Size;
import com.jakt.aiplatform.core.model.enums.IsDefaultEnum;
import com.jakt.aiplatform.core.model.enums.DictDataStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 更新字典数据请求 DTO。
 *
 * <p>校验规则与 sys_dict_data 表字段对齐：非空 + varchar 长度，不做业务自定义规则。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysDictDataUpdateRequest extends BaseRequest {
    /** 字典排序。 */
    private Long dictSort;

    /** 字典标签。 */
    @Size(max = 100, message = "字典标签长度不能超过 100")
    private String dictLabel;

    /** 字典键值。 */
    @Size(max = 100, message = "字典键值长度不能超过 100")
    private String dictValue;

    /** 字典类型。 */
    @Size(max = 100, message = "字典类型长度不能超过 100")
    private String dictType;

    /** 样式属性（其他样式扩展）。 */
    @Size(max = 100, message = "样式属性（其他样式扩展）长度不能超过 100")
    private String cssClass;

    /** 表格回显样式。 */
    @Size(max = 100, message = "表格回显样式长度不能超过 100")
    private String listClass;

    /** 是否默认（Y是 N否）。 */
    private IsDefaultEnum isDefault;

    /** 状态（0正常 1停用）。 */
    private DictDataStatusEnum status;

    /** 备注。 */
    @Size(max = 500, message = "备注长度不能超过 500")
    private String remark;

}
