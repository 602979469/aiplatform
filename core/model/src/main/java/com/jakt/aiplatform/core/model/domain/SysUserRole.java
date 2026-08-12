package com.jakt.aiplatform.core.model.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户角色关联领域模型。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserRole extends BaseModel {
    /** 主键。 */
    private Long id;

    /** 用户ID。 */
    private Long userId;

    /** 角色ID。 */
    private Long roleId;

}
