package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.repository.SysJobRepository;
import com.jakt.aiplatform.core.service.SysJobService;
import org.springframework.stereotype.Service;

/**
 * SysJob 领域服务实现（RuoYi 移植过渡：方法清空，构造器保留待嵌入 RuoYi service 逻辑）。
 */
@Service
public class SysJobServiceImpl implements SysJobService {

    /** SysJob 仓储。 */
    private final SysJobRepository jobRepository;

    public SysJobServiceImpl(SysJobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }
}
