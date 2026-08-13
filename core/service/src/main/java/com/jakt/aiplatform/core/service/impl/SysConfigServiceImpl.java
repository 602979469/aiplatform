package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.repository.SysConfigRepository;
import com.jakt.aiplatform.core.service.SysConfigService;
import org.springframework.stereotype.Service;

/**
 * 参数配置领域服务实现（RuoYi 移植过渡：方法清空，构造器保留待嵌入 RuoYi service 逻辑）。
 */
@Service
public class SysConfigServiceImpl implements SysConfigService {

    /** 参数配置仓储。 */
    private final SysConfigRepository sysConfigRepository;

    public SysConfigServiceImpl(SysConfigRepository sysConfigRepository) {
        this.sysConfigRepository = sysConfigRepository;
    }
}
