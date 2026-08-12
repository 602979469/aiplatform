package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysNoticeManager;
import com.jakt.aiplatform.core.model.domain.SysNotice;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysNoticeQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.service.SysNoticeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通知公告管理实现类
 *
 */
@Service
public class SysNoticeManagerImpl implements SysNoticeManager {

    /** 通知公告领域服务。 */
    private final SysNoticeService sysNoticeService;

    public SysNoticeManagerImpl(SysNoticeService sysNoticeService) {
        this.sysNoticeService = sysNoticeService;
    }

    @Override
    public SysNotice createSysNotice(SysNotice sysNotice) {
        SysNotice created = sysNoticeService.createSysNotice(sysNotice);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "创建通知公告成功 noticeId={}", created.getNoticeId());
        return created;
    }

    @Override
    public SysNotice getSysNotice(Long id) {
        return sysNoticeService.getSysNotice(id);
    }

    @Override
    public PageResult<SysNotice> pageSysNotices(SysNoticeQueryParam query) {
        return sysNoticeService.findPage(query);
    }

    @Override
    public List<SysNotice> listSysNotices(SysNoticeQueryParam query) {
        return sysNoticeService.findList(query);
    }

    @Override
    public void updateSysNotice(SysNotice sysNotice) {
        sysNoticeService.updateSysNotice(sysNotice);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "更新通知公告成功 noticeId={}", sysNotice.getNoticeId());
    }

    @Override
    public void updateByCondition(SysNotice sysNotice) {
        sysNoticeService.updateByCondition(sysNotice);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "按条件更新通知公告成功 noticeId={}", sysNotice.getNoticeId());
    }

    @Override
    public void deleteSysNotice(Long id) {
        sysNoticeService.deleteSysNotice(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除通知公告成功 id={}", id);
    }
}
