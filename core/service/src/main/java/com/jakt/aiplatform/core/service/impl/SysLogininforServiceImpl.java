package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.repository.SysLogininforRepository;
import com.jakt.aiplatform.core.service.SysLogininforService;
import org.springframework.stereotype.Service;

/**
 * SysLogininfor 领域服务实现（RuoYi 移植过渡：方法清空，构造器保留待嵌入 RuoYi service 逻辑）。
 */
@Service
public class SysLogininforServiceImpl implements SysLogininforService {

    /** SysLogininfor 仓储。 */
    private final SysLogininforRepository logininforRepository;

    public SysLogininforServiceImpl(SysLogininforRepository logininforRepository) {
        this.logininforRepository = logininforRepository;
    }
}
