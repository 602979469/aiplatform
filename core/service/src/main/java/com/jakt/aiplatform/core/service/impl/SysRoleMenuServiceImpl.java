package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.model.domain.SysRoleMenu;
import com.jakt.aiplatform.core.model.param.SysRoleMenuQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.repository.SysRoleMenuRepository;
import com.jakt.aiplatform.core.service.SysRoleMenuService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色菜单关联领域服务实现：承载角色菜单关联相关的业务规则。只写规则，不碰持久化细节。
 */
@Service
public class SysRoleMenuServiceImpl implements SysRoleMenuService {

    /** 角色菜单关联仓储。 */
    private final SysRoleMenuRepository sysRoleMenuRepository;

    public SysRoleMenuServiceImpl(SysRoleMenuRepository sysRoleMenuRepository) {
        this.sysRoleMenuRepository = sysRoleMenuRepository;
    }

    @Override
    public SysRoleMenu createSysRoleMenu(SysRoleMenu sysRoleMenu) {
        return sysRoleMenuRepository.insert(sysRoleMenu);
    }

    @Override
    public void updateSysRoleMenu(SysRoleMenu sysRoleMenu) {
        sysRoleMenuRepository.update(sysRoleMenu);
    }

    @Override
    public void updateByCondition(SysRoleMenu sysRoleMenu) {
        sysRoleMenuRepository.updateByCondition(sysRoleMenu);
    }

    @Override
    public void deleteSysRoleMenu(Long id) {
        sysRoleMenuRepository.deleteById(id);
    }

    @Override
    public SysRoleMenu getSysRoleMenu(Long id) {
        return sysRoleMenuRepository.findById(id);
    }

    @Override
    public PageResult<SysRoleMenu> findPage(SysRoleMenuQueryParam query) {
        return sysRoleMenuRepository.findPage(query);
    }

    @Override
    public List<SysRoleMenu> findList(SysRoleMenuQueryParam query) {
        return sysRoleMenuRepository.findList(query);
    }
}
