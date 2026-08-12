package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysMenuManager;
import com.jakt.aiplatform.core.model.domain.SysMenu;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysMenuQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.service.SysMenuService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 菜单管理实现类
 *
 */
@Service
public class SysMenuManagerImpl implements SysMenuManager {

    /** 菜单领域服务。 */
    private final SysMenuService sysMenuService;

    public SysMenuManagerImpl(SysMenuService sysMenuService) {
        this.sysMenuService = sysMenuService;
    }

    @Override
    public SysMenu createSysMenu(SysMenu sysMenu) {
        SysMenu created = sysMenuService.createSysMenu(sysMenu);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "创建菜单成功 menuId={}", created.getMenuId());
        return created;
    }

    @Override
    public SysMenu getSysMenu(Long id) {
        return sysMenuService.getSysMenu(id);
    }

    @Override
    public PageResult<SysMenu> pageSysMenus(SysMenuQueryParam query) {
        return sysMenuService.findPage(query);
    }

    @Override
    public List<SysMenu> listSysMenus(SysMenuQueryParam query) {
        return sysMenuService.findList(query);
    }

    @Override
    public void updateSysMenu(SysMenu sysMenu) {
        sysMenuService.updateSysMenu(sysMenu);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "更新菜单成功 menuId={}", sysMenu.getMenuId());
    }

    @Override
    public void updateByCondition(SysMenu sysMenu) {
        sysMenuService.updateByCondition(sysMenu);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "按条件更新菜单成功 menuId={}", sysMenu.getMenuId());
    }

    @Override
    public void deleteSysMenu(Long id) {
        sysMenuService.deleteSysMenu(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除菜单成功 id={}", id);
    }
}
