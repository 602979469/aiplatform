package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.repository.SysUserRoleRepository;
import com.jakt.aiplatform.core.service.SysUserRoleService;
import org.springframework.stereotype.Service;

/**
 * SysUserRole 领域服务实现（RuoYi 移植过渡：方法清空，构造器保留待嵌入 RuoYi service 逻辑）。
 */
@Service
public class SysUserRoleServiceImpl implements SysUserRoleService {

    /** SysUserRole 仓储。 */
    private final SysUserRoleRepository userroleRepository;

    public SysUserRoleServiceImpl(SysUserRoleRepository userroleRepository) {
        this.userroleRepository = userroleRepository;
    }
}
