package com.jakt.aiplatform.web.param;
import java.time.LocalDateTime;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import com.jakt.aiplatform.core.model.enums.UserTypeEnum;
import com.jakt.aiplatform.core.model.enums.SexEnum;
import com.jakt.aiplatform.core.model.enums.UserStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 创建用户请求 DTO。
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserCreateRequest extends BaseRequest {

    /** 部门ID。 */
    private Long deptId;

    /** 登录账号。 */
    @NotBlank(message = "登录账号不能为空")
    @Size(max = 30, message = "登录账号长度不能超过 30")
    private String loginName;

    /** 用户昵称。 */
    @Size(max = 30, message = "用户昵称长度不能超过 30")
    private String userName;

    /** 用户类型（00系统用户 01注册用户）。 */
    private UserTypeEnum userType;

    /** 用户邮箱。 */
    @Size(max = 50, message = "用户邮箱长度不能超过 50")
    private String email;

    /** 手机号码。 */
    @Size(max = 11, message = "手机号码长度不能超过 11")
    private String phonenumber;

    /** 用户性别（0男 1女 2未知）。 */
    private SexEnum sex;

    /** 头像路径。 */
    @Size(max = 100, message = "头像路径长度不能超过 100")
    private String avatar;

    /** 密码。 */
    @Size(max = 50, message = "密码长度不能超过 50")
    private String password;

    /** 盐加密。 */
    @Size(max = 20, message = "盐加密长度不能超过 20")
    private String salt;

    /** 账号状态（0正常 1停用）。 */
    private UserStatus status;

    /** 最后登录IP。 */
    @Size(max = 128, message = "最后登录IP长度不能超过 128")
    private String loginIp;

    /** 最后登录时间。 */
    private LocalDateTime loginDate;

    /** 密码最后更新时间。 */
    private LocalDateTime pwdUpdateDate;

    /** 备注。 */
    @Size(max = 500, message = "备注长度不能超过 500")
    private String remark;

}
