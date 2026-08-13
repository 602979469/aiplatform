package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysLogininforManager;
import com.jakt.aiplatform.core.service.SysLogininforService;
import org.springframework.stereotype.Service;

/**
 * SysLogininfor 管理实现类（RuoYi 移植过渡：方法清空，构造器保留待用例编排嵌入）。
 */
@Service
public class SysLogininforManagerImpl implements SysLogininforManager {

    /** SysLogininfor 领域服务。 */
    private final SysLogininforService logininforService;

    public SysLogininforManagerImpl(SysLogininforService logininforService) {
        this.logininforService = logininforService;
    }
}
