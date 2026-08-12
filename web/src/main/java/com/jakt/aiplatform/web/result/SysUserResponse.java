package com.jakt.aiplatform.web.result;

import java.time.LocalDateTime;
import com.jakt.aiplatform.core.model.enums.UserTypeEnum;
import com.jakt.aiplatform.core.model.enums.SexEnum;
import com.jakt.aiplatform.core.model.enums.UserStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 用户响应 DTO。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SysUserResponse extends BaseResult {
    /** 主键。 */
    private Long userId;

    /** 部门ID。 */
    private Long deptId;

    /** 登录账号。 */
    private String loginName;

    /** 用户昵称。 */
    private String userName;

    /** 用户类型（00系统用户 01注册用户）。 */
    private UserTypeEnum userType;

    /** 用户邮箱。 */
    private String email;

    /** 手机号码。 */
    private String phonenumber;

    /** 用户性别（0男 1女 2未知）。 */
    private SexEnum sex;

    /** 头像路径。 */
    private String avatar;

    /** 密码。 */
    private String password;

    /** 盐加密。 */
    private String salt;

    /** 账号状态（0正常 1停用）。 */
    private UserStatusEnum status;

    /** 最后登录IP。 */
    private String loginIp;

    /** 最后登录时间。 */
    private LocalDateTime loginDate;

    /** 密码最后更新时间。 */
    private LocalDateTime pwdUpdateDate;

    /** 备注。 */
    private String remark;

}
