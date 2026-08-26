package com.jakt.aiplatform.core.model.domain;
import com.jakt.aiplatform.common.framework.model.BaseModel;

import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户表领域模型。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthUser extends BaseModel {
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

    /** 状态（0启用 1停用）。 */
    private EnableStatusEnum status;

    /** 备注。 */
    private String remark;

}
