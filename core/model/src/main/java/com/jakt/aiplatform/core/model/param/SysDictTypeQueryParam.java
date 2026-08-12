package com.jakt.aiplatform.core.model.param;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 字典类型查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysDictTypeQueryParam extends PageParam {

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

    /** 创建时间起。 */
    private LocalDateTime createTimeBegin;

    /** 创建时间止。 */
    private LocalDateTime createTimeEnd;

    /** 更新时间起。 */
    private LocalDateTime updateTimeBegin;

    /** 更新时间止。 */
    private LocalDateTime updateTimeEnd;

}
