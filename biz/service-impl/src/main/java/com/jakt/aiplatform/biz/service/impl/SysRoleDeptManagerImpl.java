package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysRoleDeptManager;
import com.jakt.aiplatform.core.model.domain.SysRoleDept;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysRoleDeptQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.service.SysRoleDeptService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色部门关联管理实现类
 *
 */
@Service
public class SysRoleDeptManagerImpl implements SysRoleDeptManager {

    /** 角色部门关联领域服务。 */
    private final SysRoleDeptService sysRoleDeptService;

    public SysRoleDeptManagerImpl(SysRoleDeptService sysRoleDeptService) {
        this.sysRoleDeptService = sysRoleDeptService;
    }

    @Override
    public SysRoleDept createSysRoleDept(SysRoleDept sysRoleDept) {
        SysRoleDept created = sysRoleDeptService.createSysRoleDept(sysRoleDept);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "创建角色部门关联成功 id={}", created.getId());
        return created;
    }

    @Override
    public SysRoleDept getSysRoleDept(Long id) {
        return sysRoleDeptService.getSysRoleDept(id);
    }

    @Override
    public PageResult<SysRoleDept> pageSysRoleDepts(SysRoleDeptQueryParam query) {
        return sysRoleDeptService.findPage(query);
    }

    @Override
    public List<SysRoleDept> listSysRoleDepts(SysRoleDeptQueryParam query) {
        return sysRoleDeptService.findList(query);
    }

    @Override
    public void updateSysRoleDept(SysRoleDept sysRoleDept) {
        sysRoleDeptService.updateSysRoleDept(sysRoleDept);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "更新角色部门关联成功 id={}", sysRoleDept.getId());
    }

    @Override
    public void updateByCondition(SysRoleDept sysRoleDept) {
        sysRoleDeptService.updateByCondition(sysRoleDept);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "按条件更新角色部门关联成功 id={}", sysRoleDept.getId());
    }

    @Override
    public void deleteSysRoleDept(Long id) {
        sysRoleDeptService.deleteSysRoleDept(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除角色部门关联成功 id={}", id);
    }
}
