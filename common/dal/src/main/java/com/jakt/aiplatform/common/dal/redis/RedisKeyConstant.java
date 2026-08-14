package com.jakt.aiplatform.common.dal.redis;

/**
 * Redis key 常量：统一管理前缀与 TTL，杜绝魔法字符串。
 */
public final class RedisKeyConstant {

    /** 在线会话快照前缀：aiplatform:online:{userId}:{token}。 */
    public static final String ONLINE_PREFIX = "aiplatform:online:";

    /** Sa-Token token key 前缀（searchTokenValue 返回完整 key，需剥离）。 */
    public static final String SATOKEN_TOKEN_PREFIX = "satoken:login:token:";

    /** 在线快照 TTL（秒），与 Sa-Token token 有效期保持一致。 */
    public static final long ONLINE_TTL_SECONDS = 86400L;

    private RedisKeyConstant() {
    }
}
