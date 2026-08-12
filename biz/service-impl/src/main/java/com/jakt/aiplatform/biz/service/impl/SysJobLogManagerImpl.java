package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysJobLogManager;
import com.jakt.aiplatform.core.model.domain.SysJobLog;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysJobLogQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.service.SysJobLogService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 定时任务日志管理实现类
 *
 */
@Service
public class SysJobLogManagerImpl implements SysJobLogManager {

    /** 定时任务日志领域服务。 */
    private final SysJobLogService sysJobLogService;

    public SysJobLogManagerImpl(SysJobLogService sysJobLogService) {
        this.sysJobLogService = sysJobLogService;
    }

    @Override
    public SysJobLog createSysJobLog(SysJobLog sysJobLog) {
        SysJobLog created = sysJobLogService.createSysJobLog(sysJobLog);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "创建定时任务日志成功 jobLogId={}", created.getJobLogId());
        return created;
    }

    @Override
    public SysJobLog getSysJobLog(Long id) {
        return sysJobLogService.getSysJobLog(id);
    }

    @Override
    public PageResult<SysJobLog> pageSysJobLogs(SysJobLogQueryParam query) {
        return sysJobLogService.findPage(query);
    }

    @Override
    public List<SysJobLog> listSysJobLogs(SysJobLogQueryParam query) {
        return sysJobLogService.findList(query);
    }

    @Override
    public void updateSysJobLog(SysJobLog sysJobLog) {
        sysJobLogService.updateSysJobLog(sysJobLog);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "更新定时任务日志成功 jobLogId={}", sysJobLog.getJobLogId());
    }

    @Override
    public void updateByCondition(SysJobLog sysJobLog) {
        sysJobLogService.updateByCondition(sysJobLog);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "按条件更新定时任务日志成功 jobLogId={}", sysJobLog.getJobLogId());
    }

    @Override
    public void deleteSysJobLog(Long id) {
        sysJobLogService.deleteSysJobLog(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除定时任务日志成功 id={}", id);
    }
}
