package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysUserManager;
import com.jakt.aiplatform.core.service.SysUserService;
import org.springframework.stereotype.Service;

/**
 * 用户管理实现类（RuoYi 移植过渡：方法清空，构造器保留待用例编排嵌入）。
 */
@Service
public class SysUserManagerImpl implements SysUserManager {

    /** 用户领域服务。 */
    private final SysUserService sysUserService;

    public SysUserManagerImpl(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }
}
