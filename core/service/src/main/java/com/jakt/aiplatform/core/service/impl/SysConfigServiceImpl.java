package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.model.domain.SysConfig;
import com.jakt.aiplatform.core.model.param.SysConfigQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.repository.SysConfigRepository;
import com.jakt.aiplatform.core.service.SysConfigService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 参数配置领域服务实现：承载参数配置相关的业务规则。只写规则，不碰持久化细节。
 */
@Service
public class SysConfigServiceImpl implements SysConfigService {

    /** 参数配置仓储。 */
    private final SysConfigRepository sysConfigRepository;

    public SysConfigServiceImpl(SysConfigRepository sysConfigRepository) {
        this.sysConfigRepository = sysConfigRepository;
    }

    @Override
    public SysConfig createSysConfig(SysConfig sysConfig) {
        return sysConfigRepository.insert(sysConfig);
    }

    @Override
    public void updateSysConfig(SysConfig sysConfig) {
        sysConfigRepository.update(sysConfig);
    }

    @Override
    public void updateByCondition(SysConfig sysConfig) {
        sysConfigRepository.updateByCondition(sysConfig);
    }

    @Override
    public void deleteSysConfig(Long id) {
        sysConfigRepository.deleteById(id);
    }

    @Override
    public SysConfig getSysConfig(Long id) {
        return sysConfigRepository.findById(id);
    }

    @Override
    public PageResult<SysConfig> findPage(SysConfigQueryParam query) {
        return sysConfigRepository.findPage(query);
    }

    @Override
    public List<SysConfig> findList(SysConfigQueryParam query) {
        return sysConfigRepository.findList(query);
    }
}
