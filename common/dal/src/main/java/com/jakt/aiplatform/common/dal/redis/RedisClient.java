package com.jakt.aiplatform.common.dal.redis;

import java.util.List;

/**
 * common-dal 通用 Redis KV 客户端：只提供通用 KV 方法，业务 key 由上层传入。
 */
public interface RedisClient {

    /**
     * 写入 KV 并设置过期时间。
     *
     * @param key 缓存 key（由上层拼装，含业务前缀）
     * @param value 缓存值（序列化为 JSON 存储）
     * @param ttlSeconds 过期秒数
     */
    void set(String key, Object value, long ttlSeconds);

    /**
     * 按 key 读取并反序列化。
     *
     * @param key 缓存 key
     * @param type 目标类型
     * @param <T> 目标类型
     * @return 缓存值；不存在返回 null
     */
    <T> T get(String key, Class<T> type);

    /**
     * 批量读取并反序列化。
     *
     * @param keys 缓存 key 列表
     * @param type 目标类型
     * @param <T> 目标类型
     * @return 缓存值列表；元素顺序与入参一致，缺失项为 null
     */
    <T> List<T> multiGet(List<String> keys, Class<T> type);

    /**
     * 按 key 删除。
     *
     * @param key 缓存 key
     */
    void delete(String key);
}
