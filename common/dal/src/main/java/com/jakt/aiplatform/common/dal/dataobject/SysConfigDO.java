package com.jakt.aiplatform.common.dal.dataobject;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 参数配置 DO对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysConfigDO extends BaseDO {
    /** 主键。 */
    private Long configId;

    /** 参数名称。 */
    private String configName;

    /** 参数键名。 */
    private String configKey;

    /** 参数键值。 */
    private String configValue;

    /** 系统内置（Y是 N否）。 */
    private String configType;

    /** 备注。 */
    private String remark;

}
