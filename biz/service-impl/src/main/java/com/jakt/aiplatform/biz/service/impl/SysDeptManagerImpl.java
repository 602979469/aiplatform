package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysDeptManager;
import com.jakt.aiplatform.core.model.domain.SysDept;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysDeptQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.service.SysDeptService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 部门管理实现类
 *
 */
@Service
public class SysDeptManagerImpl implements SysDeptManager {

    /** 部门领域服务。 */
    private final SysDeptService sysDeptService;

    public SysDeptManagerImpl(SysDeptService sysDeptService) {
        this.sysDeptService = sysDeptService;
    }

    @Override
    public SysDept createSysDept(SysDept sysDept) {
        SysDept created = sysDeptService.createSysDept(sysDept);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "创建部门成功 deptId={}", created.getDeptId());
        return created;
    }

    @Override
    public SysDept getSysDept(Long id) {
        return sysDeptService.getSysDept(id);
    }

    @Override
    public PageResult<SysDept> pageSysDepts(SysDeptQueryParam query) {
        return sysDeptService.findPage(query);
    }

    @Override
    public List<SysDept> listSysDepts(SysDeptQueryParam query) {
        return sysDeptService.findList(query);
    }

    @Override
    public void updateSysDept(SysDept sysDept) {
        sysDeptService.updateSysDept(sysDept);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "更新部门成功 deptId={}", sysDept.getDeptId());
    }

    @Override
    public void updateByCondition(SysDept sysDept) {
        sysDeptService.updateByCondition(sysDept);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "按条件更新部门成功 deptId={}", sysDept.getDeptId());
    }

    @Override
    public void deleteSysDept(Long id) {
        sysDeptService.deleteSysDept(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除部门成功 id={}", id);
    }
}
