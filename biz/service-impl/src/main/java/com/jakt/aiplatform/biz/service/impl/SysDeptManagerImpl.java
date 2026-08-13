package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysDeptManager;
import com.jakt.aiplatform.core.service.SysDeptService;
import org.springframework.stereotype.Service;

/**
 * SysDept 管理实现类（RuoYi 移植过渡：方法清空，构造器保留待用例编排嵌入）。
 */
@Service
public class SysDeptManagerImpl implements SysDeptManager {

    /** SysDept 领域服务。 */
    private final SysDeptService deptService;

    public SysDeptManagerImpl(SysDeptService deptService) {
        this.deptService = deptService;
    }
}
