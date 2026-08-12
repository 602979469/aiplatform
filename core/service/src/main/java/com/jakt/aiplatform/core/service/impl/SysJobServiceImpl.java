package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.model.domain.SysJob;
import com.jakt.aiplatform.core.model.param.SysJobQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.repository.SysJobRepository;
import com.jakt.aiplatform.core.service.SysJobService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 定时任务领域服务实现：承载定时任务相关的业务规则。只写规则，不碰持久化细节。
 */
@Service
public class SysJobServiceImpl implements SysJobService {

    /** 定时任务仓储。 */
    private final SysJobRepository sysJobRepository;

    public SysJobServiceImpl(SysJobRepository sysJobRepository) {
        this.sysJobRepository = sysJobRepository;
    }

    @Override
    public SysJob createSysJob(SysJob sysJob) {
        return sysJobRepository.insert(sysJob);
    }

    @Override
    public void updateSysJob(SysJob sysJob) {
        sysJobRepository.update(sysJob);
    }

    @Override
    public void updateByCondition(SysJob sysJob) {
        sysJobRepository.updateByCondition(sysJob);
    }

    @Override
    public void deleteSysJob(Long id) {
        sysJobRepository.deleteById(id);
    }

    @Override
    public SysJob getSysJob(Long id) {
        return sysJobRepository.findById(id);
    }

    @Override
    public PageResult<SysJob> findPage(SysJobQueryParam query) {
        return sysJobRepository.findPage(query);
    }

    @Override
    public List<SysJob> findList(SysJobQueryParam query) {
        return sysJobRepository.findList(query);
    }
}
