package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysRoleDeptManager;
import com.jakt.aiplatform.core.service.SysRoleDeptService;
import org.springframework.stereotype.Service;

/**
 * SysRoleDept 管理实现类（RuoYi 移植过渡：方法清空，构造器保留待用例编排嵌入）。
 */
@Service
public class SysRoleDeptManagerImpl implements SysRoleDeptManager {

    /** SysRoleDept 领域服务。 */
    private final SysRoleDeptService roledeptService;

    public SysRoleDeptManagerImpl(SysRoleDeptService roledeptService) {
        this.roledeptService = roledeptService;
    }
}
