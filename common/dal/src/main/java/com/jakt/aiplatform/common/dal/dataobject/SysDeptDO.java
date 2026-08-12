package com.jakt.aiplatform.common.dal.dataobject;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门 DO对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysDeptDO extends BaseDO {
    /** 主键。 */
    private Long deptId;

    /** 父部门id。 */
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
    private String status;

}
