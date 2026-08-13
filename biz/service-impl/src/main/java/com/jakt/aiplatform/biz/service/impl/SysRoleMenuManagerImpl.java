package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysRoleMenuManager;
import com.jakt.aiplatform.core.service.SysRoleMenuService;
import org.springframework.stereotype.Service;

/**
 * SysRoleMenu 管理实现类（RuoYi 移植过渡：方法清空，构造器保留待用例编排嵌入）。
 */
@Service
public class SysRoleMenuManagerImpl implements SysRoleMenuManager {

    /** SysRoleMenu 领域服务。 */
    private final SysRoleMenuService rolemenuService;

    public SysRoleMenuManagerImpl(SysRoleMenuService rolemenuService) {
        this.rolemenuService = rolemenuService;
    }
}
