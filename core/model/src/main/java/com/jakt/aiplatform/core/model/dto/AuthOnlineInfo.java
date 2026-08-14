package com.jakt.aiplatform.core.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 在线用户信息 DTO（在线列表返回项，组合对象）。
 */
@Data
public class AuthOnlineInfo implements Serializable {

    /** 序列化版本号。 */
    @Serial
    private static final long serialVersionUID = 1L;

    /** token 值。 */
    private String tokenValue;

    /** 用户ID。 */
    private Long userId;

    /** 用户名。 */
    private String username;

    /** 昵称。 */
    private String nickname;

    /** 登录IP。 */
    private String loginIp;

    /** 登录时间。 */
    private LocalDateTime loginTime;
}
