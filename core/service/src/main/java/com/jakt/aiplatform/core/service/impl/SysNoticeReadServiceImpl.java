package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.repository.SysNoticeReadRepository;
import com.jakt.aiplatform.core.service.SysNoticeReadService;
import org.springframework.stereotype.Service;

/**
 * SysNoticeRead 领域服务实现（RuoYi 移植过渡：方法清空，构造器保留待嵌入 RuoYi service 逻辑）。
 */
@Service
public class SysNoticeReadServiceImpl implements SysNoticeReadService {

    /** SysNoticeRead 仓储。 */
    private final SysNoticeReadRepository noticereadRepository;

    public SysNoticeReadServiceImpl(SysNoticeReadRepository noticereadRepository) {
        this.noticereadRepository = noticereadRepository;
    }
}
