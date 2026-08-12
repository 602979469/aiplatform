package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.model.domain.SysRole;
import com.jakt.aiplatform.core.model.param.SysRoleQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.repository.SysRoleRepository;
import com.jakt.aiplatform.core.service.SysRoleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色领域服务实现：承载角色相关的业务规则。只写规则，不碰持久化细节。
 */
@Service
public class SysRoleServiceImpl implements SysRoleService {

    /** 角色仓储。 */
    private final SysRoleRepository sysRoleRepository;

    public SysRoleServiceImpl(SysRoleRepository sysRoleRepository) {
        this.sysRoleRepository = sysRoleRepository;
    }

    @Override
    public SysRole createSysRole(SysRole sysRole) {
        return sysRoleRepository.insert(sysRole);
    }

    @Override
    public void updateSysRole(SysRole sysRole) {
        sysRoleRepository.update(sysRole);
    }

    @Override
    public void updateByCondition(SysRole sysRole) {
        sysRoleRepository.updateByCondition(sysRole);
    }

    @Override
    public void deleteSysRole(Long id) {
        sysRoleRepository.deleteById(id);
    }

    @Override
    public SysRole getSysRole(Long id) {
        return sysRoleRepository.findById(id);
    }

    @Override
    public PageResult<SysRole> findPage(SysRoleQueryParam query) {
        return sysRoleRepository.findPage(query);
    }

    @Override
    public List<SysRole> findList(SysRoleQueryParam query) {
        return sysRoleRepository.findList(query);
    }
}
