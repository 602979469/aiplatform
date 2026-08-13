package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysUserOnlineManager;
import com.jakt.aiplatform.core.service.SysUserOnlineService;
import org.springframework.stereotype.Service;

/**
 * SysUserOnline 管理实现类（RuoYi 移植过渡：方法清空，构造器保留待用例编排嵌入）。
 */
@Service
public class SysUserOnlineManagerImpl implements SysUserOnlineManager {

    /** SysUserOnline 领域服务。 */
    private final SysUserOnlineService useronlineService;

    public SysUserOnlineManagerImpl(SysUserOnlineService useronlineService) {
        this.useronlineService = useronlineService;
    }
}
