package com.jakt.aiplatform.core.model.domain;

import com.jakt.aiplatform.core.model.enums.ConfigTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 参数配置领域模型（RuoYi 结构：继承 BaseEntity）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysConfig extends BaseEntity {

    /** 参数主键。 */
    private Long configId;

    /** 参数名称。 */
    private String configName;

    /** 参数键名。 */
    private String configKey;

    /** 参数键值。 */
    private String configValue;

    /** 系统内置（Y是 N否）。 */
    private ConfigTypeEnum configType;
}
