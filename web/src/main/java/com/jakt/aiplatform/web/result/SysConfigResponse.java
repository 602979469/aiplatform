package com.jakt.aiplatform.web.result;

import com.jakt.aiplatform.core.model.enums.ConfigTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 参数配置响应 DTO。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SysConfigResponse extends BaseResult {
    /** 主键。 */
    private Long configId;

    /** 参数名称。 */
    private String configName;

    /** 参数键名。 */
    private String configKey;

    /** 参数键值。 */
    private String configValue;

    /** 系统内置（Y是 N否）。 */
    private ConfigTypeEnum configType;

    /** 备注。 */
    private String remark;

}
