package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.core.model.constant.AiPlatformConstant;
import com.jakt.aiplatform.core.model.dto.AuthOnlineInfo;
import com.jakt.aiplatform.core.model.dto.AuthOnlineSnapshot;
import com.jakt.aiplatform.core.model.param.AuthOnlineQueryParam;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.repository.AuthOnlineRepository;
import com.jakt.aiplatform.core.service.AuthSessionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 在线会话领域服务实现：在线列表 = Sa-Token 在线 token + Redis 快照回填。
 */
@Service
public class AuthSessionServiceImpl implements AuthSessionService {

    private final AuthOnlineRepository authOnlineRepository;

    public AuthSessionServiceImpl(AuthOnlineRepository authOnlineRepository) {
        this.authOnlineRepository = authOnlineRepository;
    }

    @Override
    public PageResult<AuthOnlineInfo> listOnline(AuthOnlineQueryParam query) {
        List<AuthOnlineInfo> all = listAll();
        if (StrUtil.isNotBlank(query.getKeyword())) {
            all = all.stream().filter(o -> StrUtil.containsIgnoreCase(o.getUsername(), query.getKeyword())
                    || StrUtil.containsIgnoreCase(o.getNickname(), query.getKeyword())).toList();
        }
        int total = all.size();
        int pageNum = query.getPageNum();
        int pageSize = query.getPageSize();
        int from = Math.min(Math.max(pageNum - 1, 0) * pageSize, total);
        int to = Math.min(from + pageSize, total);
        return new PageResult<>(total, pageNum, pageSize, all.subList(from, to));
    }

    @Override
    public void kickout(Long userId) {
        StpUtil.kickout(userId);
    }

    @Override
    public void forceLogout(Long userId) {
        StpUtil.logout(userId);
    }

    @Override
    public void disable(Long userId, long seconds) {
        StpUtil.disable(userId, seconds);
        StpUtil.logout(userId);
    }

    @Override
    public void untieDisable(Long userId) {
        StpUtil.untieDisable(userId);
    }

    /** 查询全部在线用户（Redis 快照回填，缺失项跳过）。 */
    private List<AuthOnlineInfo> listAll() {
        // Redis 版 searchTokenValue 返回完整 key（satoken:login:token:xxx），需剥离前缀
        List<String> tokens = StpUtil.searchTokenValue("", 0, -1, true).stream()
                .map(key -> StrUtil.removePrefix(key, AiPlatformConstant.SATOKEN_TOKEN_PREFIX))
                .filter(StrUtil::isNotBlank).toList();
        List<AuthOnlineSnapshot> sourceList = authOnlineRepository.findBatch(AiPlatformConstant.ONLINE_REDIS_KEY_PREFIX, tokens);
        return ConvertUtil.map(sourceList, this::toInfo);
    }

    /** 在线快照 → 在线信息。 */
    private AuthOnlineInfo toInfo(AuthOnlineSnapshot snapshot) {
        AuthOnlineInfo info = new AuthOnlineInfo();
        info.setTokenValue(snapshot.getTokenValue());
        info.setUserId(snapshot.getUserId());
        info.setUsername(snapshot.getUsername());
        info.setNickname(snapshot.getNickname());
        info.setLoginIp(snapshot.getLoginIp());
        info.setLoginTime(snapshot.getLoginTime());
        return info;
    }
}
