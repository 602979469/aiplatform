package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.repository.SysMenuRepository;
import com.jakt.aiplatform.core.service.SysMenuService;
import org.springframework.stereotype.Service;

/**
 * SysMenu 领域服务实现（RuoYi 移植过渡：方法清空，构造器保留待嵌入 RuoYi service 逻辑）。
 */
@Service
public class SysMenuServiceImpl implements SysMenuService {

    /** SysMenu 仓储。 */
    private final SysMenuRepository menuRepository;

    public SysMenuServiceImpl(SysMenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }
}
