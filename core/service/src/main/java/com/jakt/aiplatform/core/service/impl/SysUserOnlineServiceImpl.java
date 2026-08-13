package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.repository.SysUserOnlineRepository;
import com.jakt.aiplatform.core.service.SysUserOnlineService;
import org.springframework.stereotype.Service;

/**
 * SysUserOnline 领域服务实现（RuoYi 移植过渡：方法清空，构造器保留待嵌入 RuoYi service 逻辑）。
 */
@Service
public class SysUserOnlineServiceImpl implements SysUserOnlineService {

    /** SysUserOnline 仓储。 */
    private final SysUserOnlineRepository useronlineRepository;

    public SysUserOnlineServiceImpl(SysUserOnlineRepository useronlineRepository) {
        this.useronlineRepository = useronlineRepository;
    }
}
