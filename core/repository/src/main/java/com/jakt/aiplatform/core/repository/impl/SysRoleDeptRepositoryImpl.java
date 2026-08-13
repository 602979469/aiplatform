package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysRoleDeptDO;
import com.jakt.aiplatform.common.dal.mapper.SysRoleDeptMapper;
import com.jakt.aiplatform.common.util.tools.ListUtil;
import com.jakt.aiplatform.core.model.domain.SysRoleDept;
import com.jakt.aiplatform.core.repository.SysRoleDeptRepository;
import com.jakt.aiplatform.core.repository.convertor.SysRoleDeptConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色部门关联仓储实现（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
@Repository
public class SysRoleDeptRepositoryImpl implements SysRoleDeptRepository {

    /** 角色部门关联 Mapper。 */
    private final SysRoleDeptMapper sysRoleDeptMapper;

    public SysRoleDeptRepositoryImpl(SysRoleDeptMapper sysRoleDeptMapper) {
        this.sysRoleDeptMapper = sysRoleDeptMapper;
    }

    @Override
    public int deleteRoleDeptByRoleId(Long roleId) {
        return sysRoleDeptMapper.deleteRoleDeptByRoleId(roleId);
    }

    @Override
    public int selectCountRoleDeptByDeptId(Long deptId) {
        return sysRoleDeptMapper.selectCountRoleDeptByDeptId(deptId);
    }

    @Override
    public int deleteRoleDept(Long[] ids) {
        return sysRoleDeptMapper.deleteRoleDept(ids);
    }

    @Override
    public int batchRoleDept(List<SysRoleDept> roleDeptList) {
        List<SysRoleDeptDO> doList = ListUtil.convert(roleDeptList, SysRoleDeptConvertor::toDO);
        return sysRoleDeptMapper.batchRoleDept(doList);
    }
}
