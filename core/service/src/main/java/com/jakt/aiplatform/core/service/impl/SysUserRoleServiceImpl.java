package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.model.domain.SysUserRole;
import com.jakt.aiplatform.core.model.param.SysUserRoleQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.repository.SysUserRoleRepository;
import com.jakt.aiplatform.core.service.SysUserRoleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户角色关联领域服务实现：承载用户角色关联相关的业务规则。只写规则，不碰持久化细节。
 */
@Service
public class SysUserRoleServiceImpl implements SysUserRoleService {

    /** 用户角色关联仓储。 */
    private final SysUserRoleRepository sysUserRoleRepository;

    public SysUserRoleServiceImpl(SysUserRoleRepository sysUserRoleRepository) {
        this.sysUserRoleRepository = sysUserRoleRepository;
    }

    @Override
    public SysUserRole createSysUserRole(SysUserRole sysUserRole) {
        return sysUserRoleRepository.insert(sysUserRole);
    }

    @Override
    public void updateSysUserRole(SysUserRole sysUserRole) {
        sysUserRoleRepository.update(sysUserRole);
    }

    @Override
    public void updateByCondition(SysUserRole sysUserRole) {
        sysUserRoleRepository.updateByCondition(sysUserRole);
    }

    @Override
    public void deleteSysUserRole(Long id) {
        sysUserRoleRepository.deleteById(id);
    }

    @Override
    public SysUserRole getSysUserRole(Long id) {
        return sysUserRoleRepository.findById(id);
    }

    @Override
    public PageResult<SysUserRole> findPage(SysUserRoleQueryParam query) {
        return sysUserRoleRepository.findPage(query);
    }

    @Override
    public List<SysUserRole> findList(SysUserRoleQueryParam query) {
        return sysUserRoleRepository.findList(query);
    }
}
