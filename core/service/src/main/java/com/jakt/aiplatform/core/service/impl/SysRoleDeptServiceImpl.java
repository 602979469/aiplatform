package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.model.domain.SysRoleDept;
import com.jakt.aiplatform.core.model.param.SysRoleDeptQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.repository.SysRoleDeptRepository;
import com.jakt.aiplatform.core.service.SysRoleDeptService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色部门关联领域服务实现：承载角色部门关联相关的业务规则。只写规则，不碰持久化细节。
 */
@Service
public class SysRoleDeptServiceImpl implements SysRoleDeptService {

    /** 角色部门关联仓储。 */
    private final SysRoleDeptRepository sysRoleDeptRepository;

    public SysRoleDeptServiceImpl(SysRoleDeptRepository sysRoleDeptRepository) {
        this.sysRoleDeptRepository = sysRoleDeptRepository;
    }

    @Override
    public SysRoleDept createSysRoleDept(SysRoleDept sysRoleDept) {
        return sysRoleDeptRepository.insert(sysRoleDept);
    }

    @Override
    public void updateSysRoleDept(SysRoleDept sysRoleDept) {
        sysRoleDeptRepository.update(sysRoleDept);
    }

    @Override
    public void updateByCondition(SysRoleDept sysRoleDept) {
        sysRoleDeptRepository.updateByCondition(sysRoleDept);
    }

    @Override
    public void deleteSysRoleDept(Long id) {
        sysRoleDeptRepository.deleteById(id);
    }

    @Override
    public SysRoleDept getSysRoleDept(Long id) {
        return sysRoleDeptRepository.findById(id);
    }

    @Override
    public PageResult<SysRoleDept> findPage(SysRoleDeptQueryParam query) {
        return sysRoleDeptRepository.findPage(query);
    }

    @Override
    public List<SysRoleDept> findList(SysRoleDeptQueryParam query) {
        return sysRoleDeptRepository.findList(query);
    }
}
