package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.model.domain.SysJobLog;
import com.jakt.aiplatform.core.model.param.SysJobLogQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.repository.SysJobLogRepository;
import com.jakt.aiplatform.core.service.SysJobLogService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 定时任务日志领域服务实现：承载定时任务日志相关的业务规则。只写规则，不碰持久化细节。
 */
@Service
public class SysJobLogServiceImpl implements SysJobLogService {

    /** 定时任务日志仓储。 */
    private final SysJobLogRepository sysJobLogRepository;

    public SysJobLogServiceImpl(SysJobLogRepository sysJobLogRepository) {
        this.sysJobLogRepository = sysJobLogRepository;
    }

    @Override
    public SysJobLog createSysJobLog(SysJobLog sysJobLog) {
        return sysJobLogRepository.insert(sysJobLog);
    }

    @Override
    public void updateSysJobLog(SysJobLog sysJobLog) {
        sysJobLogRepository.update(sysJobLog);
    }

    @Override
    public void updateByCondition(SysJobLog sysJobLog) {
        sysJobLogRepository.updateByCondition(sysJobLog);
    }

    @Override
    public void deleteSysJobLog(Long id) {
        sysJobLogRepository.deleteById(id);
    }

    @Override
    public SysJobLog getSysJobLog(Long id) {
        return sysJobLogRepository.findById(id);
    }

    @Override
    public PageResult<SysJobLog> findPage(SysJobLogQueryParam query) {
        return sysJobLogRepository.findPage(query);
    }

    @Override
    public List<SysJobLog> findList(SysJobLogQueryParam query) {
        return sysJobLogRepository.findList(query);
    }
}
