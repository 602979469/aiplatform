package com.jakt.aiplatform.core.model.constant;

/**
 * 全局常量：统一管理魔法值，禁止业务代码散落字符串/数字字面量。
 */
public final class AiPlatformConstant {

    /** 空字符串。 */
    public static final String EMPTY_STRING = "";

    /** 默认会话名称（AI 对话新建会话时使用）。 */
    public static final String DEFAULT_SESSION_NAME = "新会话";

    /** 封禁默认时长（秒）。 */
    public static final long DEFAULT_DISABLE_SECONDS = 1800L;

    /** 在线快照 Redis key 前缀。 */
    public static final String ONLINE_REDIS_KEY_PREFIX = "aiplatform:online:";

    /** Sa-Token token key 前缀。 */
    public static final String SATOKEN_TOKEN_PREFIX = "satoken:login:token:";

    /** 在线快照 TTL（秒）。 */
    public static final long ONLINE_TTL_SECONDS = 86400L;

    private AiPlatformConstant() {
    }
}
