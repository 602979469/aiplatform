package com.jakt.aiplatform.common.dal.dataobject.redis;

import lombok.Data;

/**
 * 在线会话快照（Redis DO）：登录时写入，供在线列表直接回填用户信息。
 *
 * <p>以 JSON 字符串存于 Redis（Hutool JSONUtil 序列化），key 为
 * {@code aiplatform:online:{userId}:{token}}。
 */
@Data
public class AuthOnlineRedisDO {

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

    /** 登录时间（ISO 字符串，避免 LocalDateTime 序列化为毫秒）。 */
    private String loginTime;
}
