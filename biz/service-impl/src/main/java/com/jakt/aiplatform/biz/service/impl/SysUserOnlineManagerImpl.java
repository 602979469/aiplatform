package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysUserOnlineManager;
import com.jakt.aiplatform.core.model.domain.SysUserOnline;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysUserOnlineQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.service.SysUserOnlineService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 在线用户管理实现类
 *
 */
@Service
public class SysUserOnlineManagerImpl implements SysUserOnlineManager {

    /** 在线用户领域服务。 */
    private final SysUserOnlineService sysUserOnlineService;

    public SysUserOnlineManagerImpl(SysUserOnlineService sysUserOnlineService) {
        this.sysUserOnlineService = sysUserOnlineService;
    }

    @Override
    public SysUserOnline createSysUserOnline(SysUserOnline sysUserOnline) {
        SysUserOnline created = sysUserOnlineService.createSysUserOnline(sysUserOnline);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "创建在线用户成功 sessionId={}", created.getSessionId());
        return created;
    }

    @Override
    public SysUserOnline getSysUserOnline(String id) {
        return sysUserOnlineService.getSysUserOnline(id);
    }

    @Override
    public PageResult<SysUserOnline> pageSysUserOnlines(SysUserOnlineQueryParam query) {
        return sysUserOnlineService.findPage(query);
    }

    @Override
    public List<SysUserOnline> listSysUserOnlines(SysUserOnlineQueryParam query) {
        return sysUserOnlineService.findList(query);
    }

    @Override
    public void updateSysUserOnline(SysUserOnline sysUserOnline) {
        sysUserOnlineService.updateSysUserOnline(sysUserOnline);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "更新在线用户成功 sessionId={}", sysUserOnline.getSessionId());
    }

    @Override
    public void updateByCondition(SysUserOnline sysUserOnline) {
        sysUserOnlineService.updateByCondition(sysUserOnline);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "按条件更新在线用户成功 sessionId={}", sysUserOnline.getSessionId());
    }

    @Override
    public void deleteSysUserOnline(String id) {
        sysUserOnlineService.deleteSysUserOnline(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除在线用户成功 id={}", id);
    }
}
