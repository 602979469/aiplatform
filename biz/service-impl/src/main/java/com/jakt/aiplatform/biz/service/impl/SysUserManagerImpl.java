package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysUserManager;
import com.jakt.aiplatform.core.model.domain.SysUser;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysUserQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.service.SysUserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户管理实现类
 *
 */
@Service
public class SysUserManagerImpl implements SysUserManager {

    /** 用户领域服务。 */
    private final SysUserService sysUserService;

    public SysUserManagerImpl(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    @Override
    public SysUser createSysUser(SysUser sysUser) {
        SysUser created = sysUserService.createSysUser(sysUser);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "创建用户成功 userId={}", created.getUserId());
        return created;
    }

    @Override
    public SysUser getSysUser(Long id) {
        return sysUserService.getSysUser(id);
    }

    @Override
    public PageResult<SysUser> pageSysUsers(SysUserQueryParam query) {
        return sysUserService.findPage(query);
    }

    @Override
    public List<SysUser> listSysUsers(SysUserQueryParam query) {
        return sysUserService.findList(query);
    }

    @Override
    public void updateSysUser(SysUser sysUser) {
        sysUserService.updateSysUser(sysUser);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "更新用户成功 userId={}", sysUser.getUserId());
    }

    @Override
    public void updateByCondition(SysUser sysUser) {
        sysUserService.updateByCondition(sysUser);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "按条件更新用户成功 userId={}", sysUser.getUserId());
    }

    @Override
    public void deleteSysUser(Long id) {
        sysUserService.deleteSysUser(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除用户成功 id={}", id);
    }
}
