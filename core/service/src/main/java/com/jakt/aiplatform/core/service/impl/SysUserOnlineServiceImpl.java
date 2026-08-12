package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.model.domain.SysUserOnline;
import com.jakt.aiplatform.core.model.param.SysUserOnlineQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.repository.SysUserOnlineRepository;
import com.jakt.aiplatform.core.service.SysUserOnlineService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 在线用户领域服务实现：承载在线用户相关的业务规则。只写规则，不碰持久化细节。
 */
@Service
public class SysUserOnlineServiceImpl implements SysUserOnlineService {

    /** 在线用户仓储。 */
    private final SysUserOnlineRepository sysUserOnlineRepository;

    public SysUserOnlineServiceImpl(SysUserOnlineRepository sysUserOnlineRepository) {
        this.sysUserOnlineRepository = sysUserOnlineRepository;
    }

    @Override
    public SysUserOnline createSysUserOnline(SysUserOnline sysUserOnline) {
        return sysUserOnlineRepository.insert(sysUserOnline);
    }

    @Override
    public void updateSysUserOnline(SysUserOnline sysUserOnline) {
        sysUserOnlineRepository.update(sysUserOnline);
    }

    @Override
    public void updateByCondition(SysUserOnline sysUserOnline) {
        sysUserOnlineRepository.updateByCondition(sysUserOnline);
    }

    @Override
    public void deleteSysUserOnline(String id) {
        sysUserOnlineRepository.deleteById(id);
    }

    @Override
    public SysUserOnline getSysUserOnline(String id) {
        return sysUserOnlineRepository.findById(id);
    }

    @Override
    public PageResult<SysUserOnline> findPage(SysUserOnlineQueryParam query) {
        return sysUserOnlineRepository.findPage(query);
    }

    @Override
    public List<SysUserOnline> findList(SysUserOnlineQueryParam query) {
        return sysUserOnlineRepository.findList(query);
    }
}
