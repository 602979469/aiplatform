package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysUserRoleManager;
import com.jakt.aiplatform.core.model.domain.SysUserRole;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysUserRoleQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.service.SysUserRoleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户角色关联管理实现类
 *
 */
@Service
public class SysUserRoleManagerImpl implements SysUserRoleManager {

    /** 用户角色关联领域服务。 */
    private final SysUserRoleService sysUserRoleService;

    public SysUserRoleManagerImpl(SysUserRoleService sysUserRoleService) {
        this.sysUserRoleService = sysUserRoleService;
    }

    @Override
    public SysUserRole createSysUserRole(SysUserRole sysUserRole) {
        SysUserRole created = sysUserRoleService.createSysUserRole(sysUserRole);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "创建用户角色关联成功 id={}", created.getId());
        return created;
    }

    @Override
    public SysUserRole getSysUserRole(Long id) {
        return sysUserRoleService.getSysUserRole(id);
    }

    @Override
    public PageResult<SysUserRole> pageSysUserRoles(SysUserRoleQueryParam query) {
        return sysUserRoleService.findPage(query);
    }

    @Override
    public List<SysUserRole> listSysUserRoles(SysUserRoleQueryParam query) {
        return sysUserRoleService.findList(query);
    }

    @Override
    public void updateSysUserRole(SysUserRole sysUserRole) {
        sysUserRoleService.updateSysUserRole(sysUserRole);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "更新用户角色关联成功 id={}", sysUserRole.getId());
    }

    @Override
    public void updateByCondition(SysUserRole sysUserRole) {
        sysUserRoleService.updateByCondition(sysUserRole);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "按条件更新用户角色关联成功 id={}", sysUserRole.getId());
    }

    @Override
    public void deleteSysUserRole(Long id) {
        sysUserRoleService.deleteSysUserRole(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除用户角色关联成功 id={}", id);
    }
}
