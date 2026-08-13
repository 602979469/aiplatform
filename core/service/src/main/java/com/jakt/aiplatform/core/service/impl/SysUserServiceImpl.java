package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.repository.SysUserRepository;
import com.jakt.aiplatform.core.service.SysUserService;
import org.springframework.stereotype.Service;

/**
 * 用户领域服务实现（RuoYi 移植过渡：方法清空，构造器保留待嵌入 RuoYi service 逻辑）。
 */
@Service
public class SysUserServiceImpl implements SysUserService {

    /** 用户仓储。 */
    private final SysUserRepository sysUserRepository;

    public SysUserServiceImpl(SysUserRepository sysUserRepository) {
        this.sysUserRepository = sysUserRepository;
    }
}
