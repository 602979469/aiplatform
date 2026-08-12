package com.jakt.aiplatform.core.model.param;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 参数配置查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysConfigQueryParam extends PageParam {

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

    /** 创建时间起。 */
    private LocalDateTime createTimeBegin;

    /** 创建时间止。 */
    private LocalDateTime createTimeEnd;

    /** 更新时间起。 */
    private LocalDateTime updateTimeBegin;

    /** 更新时间止。 */
    private LocalDateTime updateTimeEnd;

}
