package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.repository.SysRoleMenuRepository;
import com.jakt.aiplatform.core.service.SysRoleMenuService;
import org.springframework.stereotype.Service;

/**
 * SysRoleMenu 领域服务实现（RuoYi 移植过渡：方法清空，构造器保留待嵌入 RuoYi service 逻辑）。
 */
@Service
public class SysRoleMenuServiceImpl implements SysRoleMenuService {

    /** SysRoleMenu 仓储。 */
    private final SysRoleMenuRepository rolemenuRepository;

    public SysRoleMenuServiceImpl(SysRoleMenuRepository rolemenuRepository) {
        this.rolemenuRepository = rolemenuRepository;
    }
}
