package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.repository.SysNoticeRepository;
import com.jakt.aiplatform.core.service.SysNotice;
import org.springframework.stereotype.Service;

/**
 * SysNotice 领域服务实现（RuoYi 移植过渡：方法清空，构造器保留待嵌入 RuoYi service 逻辑）。
 */
@Service
public class SysNoticeServiceImpl implements SysNotice {

    /** SysNotice 仓储。 */
    private final SysNoticeRepository noticeRepository;

    public SysNoticeServiceImpl(SysNoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }
}
