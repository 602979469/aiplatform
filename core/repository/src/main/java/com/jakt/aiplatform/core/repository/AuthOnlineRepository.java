package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.dto.AuthOnlineSnapshot;

import java.util.List;

/**
 * 在线会话快照仓储：封装 Redis 快照读写（key 见 {@code RedisKeyConstant}）。
 */
public interface AuthOnlineRepository {

    /**
     * 写入在线快照（覆盖旧值并刷新 TTL）。
     *
     * @param snapshot 在线快照
     */
    void save(String keyPrefix, AuthOnlineSnapshot snapshot, long ttlSeconds);

    /**
     * 按 token 查询在线快照。
     *
     * @param tokenValue token 值
     * @return 在线快照；不存在返回 null
     */
    AuthOnlineSnapshot findByTokenValue(String keyPrefix, String tokenValue);

    /**
     * 批量查询在线快照（保持入参顺序）。
     *
     * @param tokenValues token 值列表
     * @return 快照列表（缺失项跳过）
     */
    List<AuthOnlineSnapshot> findBatch(String keyPrefix, List<String> tokenValues);

    /**
     * 删除在线快照。
     *
     * @param tokenValue token 值
     */
    void removeByTokenValue(String keyPrefix, String tokenValue);
}
