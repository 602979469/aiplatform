package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.repository.SysRoleDeptRepository;
import com.jakt.aiplatform.core.service.SysRoleDeptService;
import org.springframework.stereotype.Service;

/**
 * SysRoleDept 领域服务实现（RuoYi 移植过渡：方法清空，构造器保留待嵌入 RuoYi service 逻辑）。
 */
@Service
public class SysRoleDeptServiceImpl implements SysRoleDeptService {

    /** SysRoleDept 仓储。 */
    private final SysRoleDeptRepository roledeptRepository;

    public SysRoleDeptServiceImpl(SysRoleDeptRepository roledeptRepository) {
        this.roledeptRepository = roledeptRepository;
    }
}
