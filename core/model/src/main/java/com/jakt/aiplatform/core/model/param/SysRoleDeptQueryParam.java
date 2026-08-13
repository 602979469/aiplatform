package com.jakt.aiplatform.core.model.param;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 角色部门关联查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysRoleDeptQueryParam extends PageQueryParam {

    /** 主键。 */
    private Long id;

    /** 角色ID。 */
    private Long roleId;

    /** 部门ID。 */
    private Long deptId;

    /** 创建时间起。 */
    private LocalDateTime createTimeBegin;

    /** 创建时间止。 */
    private LocalDateTime createTimeEnd;

    /** 更新时间起。 */
    private LocalDateTime updateTimeBegin;

    /** 更新时间止。 */
    private LocalDateTime updateTimeEnd;

}
