package com.jakt.aiplatform.core.model.dto;

import com.jakt.aiplatform.core.model.domain.AuthUser;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 当前登录用户信息 DTO：用户 + 角色标识 + 权限码（组合对象）。
 */
@Data
public class AuthUserInfo implements Serializable {

    /** 序列化版本号。 */
    @Serial
    private static final long serialVersionUID = 1L;

    /** 用户。 */
    private AuthUser user;

    /** 角色标识集合。 */
    private List<String> roles;

    /** 权限码集合。 */
    private List<String> perms;
}
