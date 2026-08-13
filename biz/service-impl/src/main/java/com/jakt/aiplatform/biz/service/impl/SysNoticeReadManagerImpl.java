package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysNoticeReadManager;
import com.jakt.aiplatform.core.service.SysNoticeReadService;
import org.springframework.stereotype.Service;

/**
 * SysNoticeRead 管理实现类（RuoYi 移植过渡：方法清空，构造器保留待用例编排嵌入）。
 */
@Service
public class SysNoticeReadManagerImpl implements SysNoticeReadManager {

    /** SysNoticeRead 领域服务。 */
    private final SysNoticeReadService noticereadService;

    public SysNoticeReadManagerImpl(SysNoticeReadService noticereadService) {
        this.noticereadService = noticereadService;
    }
}
