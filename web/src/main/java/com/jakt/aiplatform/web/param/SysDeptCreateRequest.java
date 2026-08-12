package com.jakt.aiplatform.web.param;
import jakarta.validation.constraints.Size;
import com.jakt.aiplatform.core.model.enums.DeptStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 创建部门请求 DTO。
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysDeptCreateRequest extends BaseRequest {

    /** 父部门id。 */
    private Long parentId;

    /** 祖级列表。 */
    @Size(max = 50, message = "祖级列表长度不能超过 50")
    private String ancestors;

    /** 部门名称。 */
    @Size(max = 30, message = "部门名称长度不能超过 30")
    private String deptName;

    /** 显示顺序。 */
    private Integer orderNum;

    /** 负责人。 */
    @Size(max = 20, message = "负责人长度不能超过 20")
    private String leader;

    /** 联系电话。 */
    @Size(max = 11, message = "联系电话长度不能超过 11")
    private String phone;

    /** 邮箱。 */
    @Size(max = 50, message = "邮箱长度不能超过 50")
    private String email;

    /** 部门状态（0正常 1停用）。 */
    private DeptStatusEnum status;

}
