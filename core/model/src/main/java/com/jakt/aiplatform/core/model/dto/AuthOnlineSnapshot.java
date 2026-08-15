package com.jakt.aiplatform.core.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 在线会话快照领域对象。
 */
@Data
public class AuthOnlineSnapshot implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String tokenValue;

    private Long userId;

    private String username;

    private String nickname;

    private String loginIp;

    private LocalDateTime loginTime;
}
