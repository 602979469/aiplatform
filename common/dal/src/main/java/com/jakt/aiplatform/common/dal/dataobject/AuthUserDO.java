package com.jakt.aiplatform.common.dal.dataobject;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户表 DO对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthUserDO extends BaseDO {
    /** 主键。 */
    private Long userId;

    /** 登录账号。 */
    private String username;

    /** 用户昵称。 */
    private String nickname;

    /** 密码（BCrypt 哈希）。 */
    private String password;

    /** 邮箱。 */
    private String email;

    /** 头像路径。 */
    private String avatar;

    /** 状态（0正常 1停用）。 */
    private String status;

    /** 备注。 */
    private String remark;

}
