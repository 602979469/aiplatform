package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.model.domain.SysOperLog;
import com.jakt.aiplatform.core.model.param.SysOperLogQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.repository.SysOperLogRepository;
import com.jakt.aiplatform.core.service.SysOperLogService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 操作日志领域服务实现：承载操作日志相关的业务规则。只写规则，不碰持久化细节。
 */
@Service
public class SysOperLogServiceImpl implements SysOperLogService {

    /** 操作日志仓储。 */
    private final SysOperLogRepository sysOperLogRepository;

    public SysOperLogServiceImpl(SysOperLogRepository sysOperLogRepository) {
        this.sysOperLogRepository = sysOperLogRepository;
    }

    @Override
    public SysOperLog createSysOperLog(SysOperLog sysOperLog) {
        return sysOperLogRepository.insert(sysOperLog);
    }

    @Override
    public void updateSysOperLog(SysOperLog sysOperLog) {
        sysOperLogRepository.update(sysOperLog);
    }

    @Override
    public void updateByCondition(SysOperLog sysOperLog) {
        sysOperLogRepository.updateByCondition(sysOperLog);
    }

    @Override
    public void deleteSysOperLog(Long id) {
        sysOperLogRepository.deleteById(id);
    }

    @Override
    public SysOperLog getSysOperLog(Long id) {
        return sysOperLogRepository.findById(id);
    }

    @Override
    public PageResult<SysOperLog> findPage(SysOperLogQueryParam query) {
        return sysOperLogRepository.findPage(query);
    }

    @Override
    public List<SysOperLog> findList(SysOperLogQueryParam query) {
        return sysOperLogRepository.findList(query);
    }
}
