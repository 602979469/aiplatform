package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysOperLogManager;
import com.jakt.aiplatform.core.service.SysOperLogService;
import org.springframework.stereotype.Service;

/**
 * SysOperLog 管理实现类（RuoYi 移植过渡：方法清空，构造器保留待用例编排嵌入）。
 */
@Service
public class SysOperLogManagerImpl implements SysOperLogManager {

    /** SysOperLog 领域服务。 */
    private final SysOperLogService operlogService;

    public SysOperLogManagerImpl(SysOperLogService operlogService) {
        this.operlogService = operlogService;
    }
}
