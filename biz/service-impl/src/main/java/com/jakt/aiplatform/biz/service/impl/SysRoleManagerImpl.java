package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysRoleManager;
import com.jakt.aiplatform.core.model.domain.SysRole;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysRoleQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.service.SysRoleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色管理实现类
 *
 */
@Service
public class SysRoleManagerImpl implements SysRoleManager {

    /** 角色领域服务。 */
    private final SysRoleService sysRoleService;

    public SysRoleManagerImpl(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

    @Override
    public SysRole createSysRole(SysRole sysRole) {
        SysRole created = sysRoleService.createSysRole(sysRole);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "创建角色成功 roleId={}", created.getRoleId());
        return created;
    }

    @Override
    public SysRole getSysRole(Long id) {
        return sysRoleService.getSysRole(id);
    }

    @Override
    public PageResult<SysRole> pageSysRoles(SysRoleQueryParam query) {
        return sysRoleService.findPage(query);
    }

    @Override
    public List<SysRole> listSysRoles(SysRoleQueryParam query) {
        return sysRoleService.findList(query);
    }

    @Override
    public void updateSysRole(SysRole sysRole) {
        sysRoleService.updateSysRole(sysRole);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "更新角色成功 roleId={}", sysRole.getRoleId());
    }

    @Override
    public void updateByCondition(SysRole sysRole) {
        sysRoleService.updateByCondition(sysRole);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "按条件更新角色成功 roleId={}", sysRole.getRoleId());
    }

    @Override
    public void deleteSysRole(Long id) {
        sysRoleService.deleteSysRole(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除角色成功 id={}", id);
    }
}
