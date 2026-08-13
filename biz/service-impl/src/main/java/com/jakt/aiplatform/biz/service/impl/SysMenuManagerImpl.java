package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysMenuManager;
import com.jakt.aiplatform.core.service.SysMenuService;
import org.springframework.stereotype.Service;

/**
 * SysMenu 管理实现类（RuoYi 移植过渡：方法清空，构造器保留待用例编排嵌入）。
 */
@Service
public class SysMenuManagerImpl implements SysMenuManager {

    /** SysMenu 领域服务。 */
    private final SysMenuService menuService;

    public SysMenuManagerImpl(SysMenuService menuService) {
        this.menuService = menuService;
    }
}
