package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysRoleMenuManager;
import com.jakt.aiplatform.core.model.domain.SysRoleMenu;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysRoleMenuQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.service.SysRoleMenuService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色菜单关联管理实现类
 *
 */
@Service
public class SysRoleMenuManagerImpl implements SysRoleMenuManager {

    /** 角色菜单关联领域服务。 */
    private final SysRoleMenuService sysRoleMenuService;

    public SysRoleMenuManagerImpl(SysRoleMenuService sysRoleMenuService) {
        this.sysRoleMenuService = sysRoleMenuService;
    }

    @Override
    public SysRoleMenu createSysRoleMenu(SysRoleMenu sysRoleMenu) {
        SysRoleMenu created = sysRoleMenuService.createSysRoleMenu(sysRoleMenu);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "创建角色菜单关联成功 id={}", created.getId());
        return created;
    }

    @Override
    public SysRoleMenu getSysRoleMenu(Long id) {
        return sysRoleMenuService.getSysRoleMenu(id);
    }

    @Override
    public PageResult<SysRoleMenu> pageSysRoleMenus(SysRoleMenuQueryParam query) {
        return sysRoleMenuService.findPage(query);
    }

    @Override
    public List<SysRoleMenu> listSysRoleMenus(SysRoleMenuQueryParam query) {
        return sysRoleMenuService.findList(query);
    }

    @Override
    public void updateSysRoleMenu(SysRoleMenu sysRoleMenu) {
        sysRoleMenuService.updateSysRoleMenu(sysRoleMenu);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "更新角色菜单关联成功 id={}", sysRoleMenu.getId());
    }

    @Override
    public void updateByCondition(SysRoleMenu sysRoleMenu) {
        sysRoleMenuService.updateByCondition(sysRoleMenu);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "按条件更新角色菜单关联成功 id={}", sysRoleMenu.getId());
    }

    @Override
    public void deleteSysRoleMenu(Long id) {
        sysRoleMenuService.deleteSysRoleMenu(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除角色菜单关联成功 id={}", id);
    }
}
