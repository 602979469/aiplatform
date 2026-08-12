package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.model.domain.SysUser;
import com.jakt.aiplatform.core.model.param.SysUserQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.repository.SysUserRepository;
import com.jakt.aiplatform.core.service.SysUserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户领域服务实现：承载用户相关的业务规则。只写规则，不碰持久化细节。
 */
@Service
public class SysUserServiceImpl implements SysUserService {

    /** 用户仓储。 */
    private final SysUserRepository sysUserRepository;

    public SysUserServiceImpl(SysUserRepository sysUserRepository) {
        this.sysUserRepository = sysUserRepository;
    }

    @Override
    public SysUser createSysUser(SysUser sysUser) {
        return sysUserRepository.insert(sysUser);
    }

    @Override
    public void updateSysUser(SysUser sysUser) {
        sysUserRepository.update(sysUser);
    }

    @Override
    public void updateByCondition(SysUser sysUser) {
        sysUserRepository.updateByCondition(sysUser);
    }

    @Override
    public void deleteSysUser(Long id) {
        sysUserRepository.deleteById(id);
    }

    @Override
    public SysUser getSysUser(Long id) {
        return sysUserRepository.findById(id);
    }

    @Override
    public PageResult<SysUser> findPage(SysUserQueryParam query) {
        return sysUserRepository.findPage(query);
    }

    @Override
    public List<SysUser> findList(SysUserQueryParam query) {
        return sysUserRepository.findList(query);
    }
}
