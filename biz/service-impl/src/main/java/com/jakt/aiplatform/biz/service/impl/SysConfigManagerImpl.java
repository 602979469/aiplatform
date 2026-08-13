package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysConfigManager;
import com.jakt.aiplatform.core.service.SysConfigService;
import org.springframework.stereotype.Service;

/**
 * 参数配置管理实现类（RuoYi 移植过渡：方法清空，构造器保留待用例编排嵌入）。
 */
@Service
public class SysConfigManagerImpl implements SysConfigManager {

    /** 参数配置领域服务。 */
    private final SysConfigService sysConfigService;

    public SysConfigManagerImpl(SysConfigService sysConfigService) {
        this.sysConfigService = sysConfigService;
    }
}
