package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysJobManager;
import com.jakt.aiplatform.core.service.SysJobService;
import org.springframework.stereotype.Service;

/**
 * SysJob 管理实现类（RuoYi 移植过渡：方法清空，构造器保留待用例编排嵌入）。
 */
@Service
public class SysJobManagerImpl implements SysJobManager {

    /** SysJob 领域服务。 */
    private final SysJobService jobService;

    public SysJobManagerImpl(SysJobService jobService) {
        this.jobService = jobService;
    }
}
