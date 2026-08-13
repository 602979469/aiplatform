package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysUserRoleManager;
import com.jakt.aiplatform.core.service.SysUserRoleService;
import org.springframework.stereotype.Service;

/**
 * SysUserRole 管理实现类（RuoYi 移植过渡：方法清空，构造器保留待用例编排嵌入）。
 */
@Service
public class SysUserRoleManagerImpl implements SysUserRoleManager {

    /** SysUserRole 领域服务。 */
    private final SysUserRoleService userroleService;

    public SysUserRoleManagerImpl(SysUserRoleService userroleService) {
        this.userroleService = userroleService;
    }
}
