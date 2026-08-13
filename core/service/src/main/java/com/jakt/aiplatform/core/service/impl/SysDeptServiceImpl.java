package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.repository.SysDeptRepository;
import com.jakt.aiplatform.core.service.SysDeptService;
import org.springframework.stereotype.Service;

/**
 * SysDept 领域服务实现（RuoYi 移植过渡：方法清空，构造器保留待嵌入 RuoYi service 逻辑）。
 */
@Service
public class SysDeptServiceImpl implements SysDeptService {

    /** SysDept 仓储。 */
    private final SysDeptRepository deptRepository;

    public SysDeptServiceImpl(SysDeptRepository deptRepository) {
        this.deptRepository = deptRepository;
    }
}
