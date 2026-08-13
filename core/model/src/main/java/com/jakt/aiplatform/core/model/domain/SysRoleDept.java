package com.jakt.aiplatform.core.model.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色部门关联领域模型（RuoYi 结构：继承 BaseEntity，含适配代理主键 id）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysRoleDept extends BaseEntity {

    /** 主键ID（适配 DDL 代理主键）。 */
    private Long id;

    /** 角色ID。 */
    private Long roleId;

    /** 部门ID。 */
    private Long deptId;
}
