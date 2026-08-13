package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysNoticeManager;
import com.jakt.aiplatform.core.service.SysNotice;
import org.springframework.stereotype.Service;

/**
 * SysNotice 管理实现类（RuoYi 移植过渡：方法清空，构造器保留待用例编排嵌入）。
 */
@Service
public class SysNoticeManagerImpl implements SysNoticeManager {

    /** SysNotice 领域服务。 */
    private final SysNotice noticeService;

    public SysNoticeManagerImpl(SysNotice noticeService) {
        this.noticeService = noticeService;
    }
}
