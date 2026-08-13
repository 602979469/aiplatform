package com.jakt.aiplatform.core.model.domain;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jakt.aiplatform.core.model.enums.SexEnum;
import com.jakt.aiplatform.core.model.enums.UserStatus;
import com.jakt.aiplatform.core.model.enums.UserTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户领域模型（RuoYi 结构：继承 BaseEntity，含组装字段 dept/roles/roleIds/postIds）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUser extends BaseEntity {

    /**
     * 是否管理员。
     *
     * @return 是否管理员
     */
    public boolean isAdmin() {
        return isAdmin(this.userId);
    }

    /**
     * 是否管理员（userId=1）。
     *
     * @param userId 用户ID
     * @return 是否管理员
     */
    public static boolean isAdmin(Long userId) {
        return userId != null && 1L == userId;
    }

    /** 用户ID。 */
    private Long userId;

    /** 部门ID。 */
    private Long deptId;

    /** 部门父ID。 */
    private Long parentId;

    /** 角色ID。 */
    private Long roleId;

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

    /** 用户头像。 */
    private String avatar;

    /** 密码（敏感字段，序列化忽略）。 */
    @JsonIgnore
    private String password;

    /** 盐加密（敏感字段，序列化忽略）。 */
    @JsonIgnore
    private String salt;

    /** 账号状态（0正常 1停用 2删除）。 */
    private UserStatus status;

    /** 删除标志（0代表存在 2代表删除）。 */
    private String delFlag;

    /** 最后登录IP。 */
    private String loginIp;

    /** 最后登录时间。 */
    private LocalDateTime loginDate;

    /** 密码最后更新时间。 */
    private LocalDateTime pwdUpdateDate;

    /** 部门对象（组装字段）。 */
    private SysDept dept;

    /** 角色集合（组装字段）。 */
    private List<SysRole> roles;

    /** 角色组（组装字段）。 */
    private Long[] roleIds;

    /** 岗位组（组装字段）。 */
    private Long[] postIds;
}
