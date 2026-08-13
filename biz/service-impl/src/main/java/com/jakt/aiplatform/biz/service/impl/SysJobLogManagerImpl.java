package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysJobLogManager;
import com.jakt.aiplatform.core.service.SysJobLogService;
import org.springframework.stereotype.Service;

/**
 * SysJobLog 管理实现类（RuoYi 移植过渡：方法清空，构造器保留待用例编排嵌入）。
 */
@Service
public class SysJobLogManagerImpl implements SysJobLogManager {

    /** SysJobLog 领域服务。 */
    private final SysJobLogService joblogService;

    public SysJobLogManagerImpl(SysJobLogService joblogService) {
        this.joblogService = joblogService;
    }
}
