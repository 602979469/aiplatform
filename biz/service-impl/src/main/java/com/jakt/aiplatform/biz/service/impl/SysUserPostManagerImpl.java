package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysUserPostManager;
import com.jakt.aiplatform.core.service.SysUserPostService;
import org.springframework.stereotype.Service;

/**
 * SysUserPost 管理实现类（RuoYi 移植过渡：方法清空，构造器保留待用例编排嵌入）。
 */
@Service
public class SysUserPostManagerImpl implements SysUserPostManager {

    /** SysUserPost 领域服务。 */
    private final SysUserPostService userpostService;

    public SysUserPostManagerImpl(SysUserPostService userpostService) {
        this.userpostService = userpostService;
    }
}
