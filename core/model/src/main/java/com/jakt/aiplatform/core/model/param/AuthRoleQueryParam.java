package com.jakt.aiplatform.core.model.param;

import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 角色表查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthRoleQueryParam extends PageParam {

    /** 主键。 */
    private Long roleId;

    /** 角色名称。 */
    private String roleName;

    /** 角色权限字符串。 */
    private String roleKey;

    /** 显示顺序。 */
    private Integer roleSort;

    /** 状态（0启用 1停用）。 */
    private EnableStatusEnum status;

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
