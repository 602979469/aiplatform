package com.jakt.aiplatform.core.model.param;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 角色查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysRoleQueryParam extends PageParam {

    /** 主键。 */
    private Long roleId;

    /** 角色名称。 */
    private String roleName;

    /** 角色权限字符串。 */
    private String roleKey;

    /** 显示顺序。 */
    private Integer roleSort;

    /** 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）。 */
    private String dataScope;

    /** 角色状态（0正常 1停用）。 */
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
