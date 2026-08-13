package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.repository.SysOperLogRepository;
import com.jakt.aiplatform.core.service.SysOperLogService;
import org.springframework.stereotype.Service;

/**
 * SysOperLog 领域服务实现（RuoYi 移植过渡：方法清空，构造器保留待嵌入 RuoYi service 逻辑）。
 */
@Service
public class SysOperLogServiceImpl implements SysOperLogService {

    /** SysOperLog 仓储。 */
    private final SysOperLogRepository operlogRepository;

    public SysOperLogServiceImpl(SysOperLogRepository operlogRepository) {
        this.operlogRepository = operlogRepository;
    }
}
