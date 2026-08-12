package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.model.domain.SysMenu;
import com.jakt.aiplatform.core.model.param.SysMenuQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.repository.SysMenuRepository;
import com.jakt.aiplatform.core.service.SysMenuService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 菜单领域服务实现：承载菜单相关的业务规则。只写规则，不碰持久化细节。
 */
@Service
public class SysMenuServiceImpl implements SysMenuService {

    /** 菜单仓储。 */
    private final SysMenuRepository sysMenuRepository;

    public SysMenuServiceImpl(SysMenuRepository sysMenuRepository) {
        this.sysMenuRepository = sysMenuRepository;
    }

    @Override
    public SysMenu createSysMenu(SysMenu sysMenu) {
        return sysMenuRepository.insert(sysMenu);
    }

    @Override
    public void updateSysMenu(SysMenu sysMenu) {
        sysMenuRepository.update(sysMenu);
    }

    @Override
    public void updateByCondition(SysMenu sysMenu) {
        sysMenuRepository.updateByCondition(sysMenu);
    }

    @Override
    public void deleteSysMenu(Long id) {
        sysMenuRepository.deleteById(id);
    }

    @Override
    public SysMenu getSysMenu(Long id) {
        return sysMenuRepository.findById(id);
    }

    @Override
    public PageResult<SysMenu> findPage(SysMenuQueryParam query) {
        return sysMenuRepository.findPage(query);
    }

    @Override
    public List<SysMenu> findList(SysMenuQueryParam query) {
        return sysMenuRepository.findList(query);
    }
}
