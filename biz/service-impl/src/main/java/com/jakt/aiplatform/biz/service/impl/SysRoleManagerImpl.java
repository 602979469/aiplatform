package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysRoleManager;
import com.jakt.aiplatform.core.service.SysRoleService;
import org.springframework.stereotype.Service;

/**
 * SysRole 管理实现类（RuoYi 移植过渡：方法清空，构造器保留待用例编排嵌入）。
 */
@Service
public class SysRoleManagerImpl implements SysRoleManager {

    /** SysRole 领域服务。 */
    private final SysRoleService roleService;

    public SysRoleManagerImpl(SysRoleService roleService) {
        this.roleService = roleService;
    }
}
