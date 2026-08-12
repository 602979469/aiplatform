package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysJobManager;
import com.jakt.aiplatform.core.model.domain.SysJob;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysJobQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.service.SysJobService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 定时任务管理实现类
 *
 */
@Service
public class SysJobManagerImpl implements SysJobManager {

    /** 定时任务领域服务。 */
    private final SysJobService sysJobService;

    public SysJobManagerImpl(SysJobService sysJobService) {
        this.sysJobService = sysJobService;
    }

    @Override
    public SysJob createSysJob(SysJob sysJob) {
        SysJob created = sysJobService.createSysJob(sysJob);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "创建定时任务成功 jobId={}", created.getJobId());
        return created;
    }

    @Override
    public SysJob getSysJob(Long id) {
        return sysJobService.getSysJob(id);
    }

    @Override
    public PageResult<SysJob> pageSysJobs(SysJobQueryParam query) {
        return sysJobService.findPage(query);
    }

    @Override
    public List<SysJob> listSysJobs(SysJobQueryParam query) {
        return sysJobService.findList(query);
    }

    @Override
    public void updateSysJob(SysJob sysJob) {
        sysJobService.updateSysJob(sysJob);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "更新定时任务成功 jobId={}", sysJob.getJobId());
    }

    @Override
    public void updateByCondition(SysJob sysJob) {
        sysJobService.updateByCondition(sysJob);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "按条件更新定时任务成功 jobId={}", sysJob.getJobId());
    }

    @Override
    public void deleteSysJob(Long id) {
        sysJobService.deleteSysJob(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除定时任务成功 id={}", id);
    }
}
