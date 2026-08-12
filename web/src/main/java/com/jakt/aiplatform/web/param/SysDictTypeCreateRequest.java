package com.jakt.aiplatform.web.param;
import jakarta.validation.constraints.Size;
import com.jakt.aiplatform.core.model.enums.DictTypeStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 创建字典类型请求 DTO。
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysDictTypeCreateRequest extends BaseRequest {

    /** 字典名称。 */
    @Size(max = 100, message = "字典名称长度不能超过 100")
    private String dictName;

    /** 字典类型。 */
    @Size(max = 100, message = "字典类型长度不能超过 100")
    private String dictType;

    /** 状态（0正常 1停用）。 */
    private DictTypeStatusEnum status;

    /** 备注。 */
    @Size(max = 500, message = "备注长度不能超过 500")
    private String remark;

}
