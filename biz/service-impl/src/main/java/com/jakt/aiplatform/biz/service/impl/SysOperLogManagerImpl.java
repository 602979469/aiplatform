package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysOperLogManager;
import com.jakt.aiplatform.core.model.domain.SysOperLog;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysOperLogQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.service.SysOperLogService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 操作日志管理实现类
 *
 */
@Service
public class SysOperLogManagerImpl implements SysOperLogManager {

    /** 操作日志领域服务。 */
    private final SysOperLogService sysOperLogService;

    public SysOperLogManagerImpl(SysOperLogService sysOperLogService) {
        this.sysOperLogService = sysOperLogService;
    }

    @Override
    public SysOperLog createSysOperLog(SysOperLog sysOperLog) {
        SysOperLog created = sysOperLogService.createSysOperLog(sysOperLog);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "创建操作日志成功 operId={}", created.getOperId());
        return created;
    }

    @Override
    public SysOperLog getSysOperLog(Long id) {
        return sysOperLogService.getSysOperLog(id);
    }

    @Override
    public PageResult<SysOperLog> pageSysOperLogs(SysOperLogQueryParam query) {
        return sysOperLogService.findPage(query);
    }

    @Override
    public List<SysOperLog> listSysOperLogs(SysOperLogQueryParam query) {
        return sysOperLogService.findList(query);
    }

    @Override
    public void updateSysOperLog(SysOperLog sysOperLog) {
        sysOperLogService.updateSysOperLog(sysOperLog);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "更新操作日志成功 operId={}", sysOperLog.getOperId());
    }

    @Override
    public void updateByCondition(SysOperLog sysOperLog) {
        sysOperLogService.updateByCondition(sysOperLog);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "按条件更新操作日志成功 operId={}", sysOperLog.getOperId());
    }

    @Override
    public void deleteSysOperLog(Long id) {
        sysOperLogService.deleteSysOperLog(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除操作日志成功 id={}", id);
    }
}
