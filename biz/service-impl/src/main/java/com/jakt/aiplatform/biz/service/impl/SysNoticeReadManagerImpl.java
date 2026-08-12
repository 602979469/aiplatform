package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysNoticeReadManager;
import com.jakt.aiplatform.core.model.domain.SysNoticeRead;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysNoticeReadQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.service.SysNoticeReadService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 公告已读记录管理实现类
 *
 */
@Service
public class SysNoticeReadManagerImpl implements SysNoticeReadManager {

    /** 公告已读记录领域服务。 */
    private final SysNoticeReadService sysNoticeReadService;

    public SysNoticeReadManagerImpl(SysNoticeReadService sysNoticeReadService) {
        this.sysNoticeReadService = sysNoticeReadService;
    }

    @Override
    public SysNoticeRead createSysNoticeRead(SysNoticeRead sysNoticeRead) {
        SysNoticeRead created = sysNoticeReadService.createSysNoticeRead(sysNoticeRead);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "创建公告已读记录成功 readId={}", created.getReadId());
        return created;
    }

    @Override
    public SysNoticeRead getSysNoticeRead(Long id) {
        return sysNoticeReadService.getSysNoticeRead(id);
    }

    @Override
    public PageResult<SysNoticeRead> pageSysNoticeReads(SysNoticeReadQueryParam query) {
        return sysNoticeReadService.findPage(query);
    }

    @Override
    public List<SysNoticeRead> listSysNoticeReads(SysNoticeReadQueryParam query) {
        return sysNoticeReadService.findList(query);
    }

    @Override
    public void updateSysNoticeRead(SysNoticeRead sysNoticeRead) {
        sysNoticeReadService.updateSysNoticeRead(sysNoticeRead);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "更新公告已读记录成功 readId={}", sysNoticeRead.getReadId());
    }

    @Override
    public void updateByCondition(SysNoticeRead sysNoticeRead) {
        sysNoticeReadService.updateByCondition(sysNoticeRead);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "按条件更新公告已读记录成功 readId={}", sysNoticeRead.getReadId());
    }

    @Override
    public void deleteSysNoticeRead(Long id) {
        sysNoticeReadService.deleteSysNoticeRead(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除公告已读记录成功 id={}", id);
    }
}
