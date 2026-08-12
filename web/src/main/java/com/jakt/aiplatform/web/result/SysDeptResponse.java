package com.jakt.aiplatform.web.result;

import com.jakt.aiplatform.core.model.enums.DeptStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 部门响应 DTO。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SysDeptResponse extends BaseResult {
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
    private DeptStatusEnum status;

}
