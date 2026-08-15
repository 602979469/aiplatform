package com.jakt.aiplatform.core.repository.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.convert.Convert;
import com.jakt.aiplatform.common.dal.dataobject.redis.AuthOnlineRedisDO;
import com.jakt.aiplatform.common.dal.redis.RedisClient;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.core.model.dto.AuthOnlineSnapshot;
import com.jakt.aiplatform.core.repository.AuthOnlineRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 在线会话快照仓储实现：StringRedisTemplate + Hutool JSON 序列化。
 */
@Repository
public class AuthOnlineRepositoryImpl implements AuthOnlineRepository {

    private final RedisClient redisClient;

    public AuthOnlineRepositoryImpl(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    @Override
    public void save(String keyPrefix, AuthOnlineSnapshot snapshot, long ttlSeconds) {
        redisClient.set(keyPrefix + snapshot.getTokenValue(), toRedisDO(snapshot), ttlSeconds);
    }

    @Override
    public AuthOnlineSnapshot findByTokenValue(String keyPrefix, String tokenValue) {
        AuthOnlineRedisDO redisDO = redisClient.get(keyPrefix + tokenValue, AuthOnlineRedisDO.class);
        return toSnapshot(redisDO);
    }

    @Override
    public List<AuthOnlineSnapshot> findBatch(String keyPrefix, List<String> tokenValues) {
        if (CollUtil.isEmpty(tokenValues)) {
            return List.of();
        }
        List<String> keys = ConvertUtil.map(tokenValues, token -> keyPrefix + token);
        List<AuthOnlineRedisDO> doList = redisClient.multiGet(keys, AuthOnlineRedisDO.class);
        return ConvertUtil.map(doList, this::toSnapshot);
    }

    @Override
    public void removeByTokenValue(String keyPrefix, String tokenValue) {
        redisClient.delete(keyPrefix + tokenValue);
    }

    private AuthOnlineRedisDO toRedisDO(AuthOnlineSnapshot snapshot) {
        AuthOnlineRedisDO target = new AuthOnlineRedisDO();
        target.setTokenValue(snapshot.getTokenValue());
        target.setUserId(snapshot.getUserId());
        target.setUsername(snapshot.getUsername());
        target.setNickname(snapshot.getNickname());
        target.setLoginIp(snapshot.getLoginIp());
        target.setLoginTime(snapshot.getLoginTime() == null ? null : snapshot.getLoginTime().toString());
        return target;
    }

    private AuthOnlineSnapshot toSnapshot(AuthOnlineRedisDO source) {
        if (source == null) {
            return null;
        }
        AuthOnlineSnapshot target = new AuthOnlineSnapshot();
        target.setTokenValue(source.getTokenValue());
        target.setUserId(source.getUserId());
        target.setUsername(source.getUsername());
        target.setNickname(source.getNickname());
        target.setLoginIp(source.getLoginIp());
        target.setLoginTime(StrUtil.isBlank(source.getLoginTime()) ? null : Convert.toLocalDateTime(source.getLoginTime()));
        return target;
    }
}
