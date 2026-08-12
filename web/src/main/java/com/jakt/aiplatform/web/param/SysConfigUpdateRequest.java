package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.Size;
import com.jakt.aiplatform.core.model.enums.ConfigTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 更新参数配置请求 DTO。
 *
 * <p>校验规则与 sys_config 表字段对齐：非空 + varchar 长度，不做业务自定义规则。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysConfigUpdateRequest extends BaseRequest {
    /** 参数名称。 */
    @Size(max = 100, message = "参数名称长度不能超过 100")
    private String configName;

    /** 参数键名。 */
    @Size(max = 100, message = "参数键名长度不能超过 100")
    private String configKey;

    /** 参数键值。 */
    @Size(max = 500, message = "参数键值长度不能超过 500")
    private String configValue;

    /** 系统内置（Y是 N否）。 */
    private ConfigTypeEnum configType;

    /** 备注。 */
    @Size(max = 500, message = "备注长度不能超过 500")
    private String remark;

}
