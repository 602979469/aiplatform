package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.repository.SysJobLogRepository;
import com.jakt.aiplatform.core.service.SysJobLogService;
import org.springframework.stereotype.Service;

/**
 * SysJobLog 领域服务实现（RuoYi 移植过渡：方法清空，构造器保留待嵌入 RuoYi service 逻辑）。
 */
@Service
public class SysJobLogServiceImpl implements SysJobLogService {

    /** SysJobLog 仓储。 */
    private final SysJobLogRepository joblogRepository;

    public SysJobLogServiceImpl(SysJobLogRepository joblogRepository) {
        this.joblogRepository = joblogRepository;
    }
}
