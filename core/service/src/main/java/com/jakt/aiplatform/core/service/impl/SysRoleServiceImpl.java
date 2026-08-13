package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.repository.SysRoleRepository;
import com.jakt.aiplatform.core.service.SysRoleService;
import org.springframework.stereotype.Service;

/**
 * SysRole 领域服务实现（RuoYi 移植过渡：方法清空，构造器保留待嵌入 RuoYi service 逻辑）。
 */
@Service
public class SysRoleServiceImpl implements SysRoleService {

    /** SysRole 仓储。 */
    private final SysRoleRepository roleRepository;

    public SysRoleServiceImpl(SysRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
}
