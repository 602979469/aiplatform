package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.model.domain.SysLogininfor;
import com.jakt.aiplatform.core.model.param.SysLogininforQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.repository.SysLogininforRepository;
import com.jakt.aiplatform.core.service.SysLogininforService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 登录日志领域服务实现：承载登录日志相关的业务规则。只写规则，不碰持久化细节。
 */
@Service
public class SysLogininforServiceImpl implements SysLogininforService {

    /** 登录日志仓储。 */
    private final SysLogininforRepository sysLogininforRepository;

    public SysLogininforServiceImpl(SysLogininforRepository sysLogininforRepository) {
        this.sysLogininforRepository = sysLogininforRepository;
    }

    @Override
    public SysLogininfor createSysLogininfor(SysLogininfor sysLogininfor) {
        return sysLogininforRepository.insert(sysLogininfor);
    }

    @Override
    public void updateSysLogininfor(SysLogininfor sysLogininfor) {
        sysLogininforRepository.update(sysLogininfor);
    }

    @Override
    public void updateByCondition(SysLogininfor sysLogininfor) {
        sysLogininforRepository.updateByCondition(sysLogininfor);
    }

    @Override
    public void deleteSysLogininfor(Long id) {
        sysLogininforRepository.deleteById(id);
    }

    @Override
    public SysLogininfor getSysLogininfor(Long id) {
        return sysLogininforRepository.findById(id);
    }

    @Override
    public PageResult<SysLogininfor> findPage(SysLogininforQueryParam query) {
        return sysLogininforRepository.findPage(query);
    }

    @Override
    public List<SysLogininfor> findList(SysLogininforQueryParam query) {
        return sysLogininforRepository.findList(query);
    }
}
