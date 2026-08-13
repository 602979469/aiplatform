package com.jakt.aiplatform.core.model.domain;

import com.jakt.aiplatform.core.model.enums.DeptStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门领域模型（RuoYi 结构：继承 BaseEntity，含组装字段 parentName/excludeId）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysDept extends BaseEntity {

    /** 部门ID。 */
    private Long deptId;

    /** 父部门ID。 */
    private Long parentId;

    /** 祖级列表。 */
    private String ancestors;

    /** 部门名称。 */
    private String deptName;

    /** 显示顺序。 */
    private Integer orderNum;

    /** 负责人。 */
    private String leader;

    /** 联系电话。 */
    private String phone;

    /** 邮箱。 */
    private String email;

    /** 部门状态（0正常 1停用）。 */
    private DeptStatusEnum status;

    /** 删除标志（0代表存在 2代表删除）。 */
    private String delFlag;

    /** 父部门名称（组装字段）。 */
    private String parentName;

    /** 排除部门ID（组装字段，序列化忽略，与 RuoYi 对齐）。 */
    @JsonIgnore
    private Long excludeId;
}
