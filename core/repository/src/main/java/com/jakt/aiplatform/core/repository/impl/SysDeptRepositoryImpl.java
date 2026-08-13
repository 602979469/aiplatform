package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysDeptDO;
import com.jakt.aiplatform.common.dal.mapper.SysDeptMapper;
import com.jakt.aiplatform.common.util.tools.ListUtil;
import com.jakt.aiplatform.core.model.domain.SysDept;
import com.jakt.aiplatform.core.model.param.SysDeptQueryParam;
import com.jakt.aiplatform.core.repository.SysDeptRepository;
import com.jakt.aiplatform.core.repository.convertor.SysDeptConvertor;
import cn.hutool.core.util.ObjectUtil;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 部门仓储实现（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
@Repository
public class SysDeptRepositoryImpl implements SysDeptRepository {

    /** 部门 Mapper。 */
    private final SysDeptMapper sysDeptMapper;

    public SysDeptRepositoryImpl(SysDeptMapper sysDeptMapper) {
        this.sysDeptMapper = sysDeptMapper;
    }

    @Override
    public int selectDeptCount(SysDept dept) {
        return sysDeptMapper.selectDeptCount(SysDeptConvertor.toQueryParam(dept));
    }

    @Override
    public int checkDeptExistUser(Long deptId) {
        return sysDeptMapper.checkDeptExistUser(deptId);
    }

    @Override
    public List<SysDept> selectDeptList(SysDept dept) {
        List<SysDeptDO> list = sysDeptMapper.selectDeptList(SysDeptConvertor.toQueryParam(dept));
        return ListUtil.convert(list, SysDeptConvertor::toModel);
    }

    @Override
    public int deleteDeptById(Long deptId) {
        return sysDeptMapper.deleteById(deptId);
    }

    @Override
    public int insertDept(SysDept dept) {
        return sysDeptMapper.insert(SysDeptConvertor.toDO(dept));
    }

    @Override
    public int updateDept(SysDept dept) {
        return sysDeptMapper.update(SysDeptConvertor.toDO(dept));
    }

    @Override
    public int updateDeptChildren(List<SysDept> depts) {
        List<SysDeptDO> doList = ListUtil.convert(depts, SysDeptConvertor::toDO);
        return sysDeptMapper.updateDeptChildren(doList);
    }

    @Override
    public SysDept selectDeptById(Long deptId) {
        return SysDeptConvertor.toModel(sysDeptMapper.selectDeptById(deptId));
    }

    @Override
    public boolean checkDeptNameUnique(SysDept dept) {
        SysDeptDO target = sysDeptMapper.checkDeptNameUnique(dept.getDeptName(), dept.getParentId());
        if (target == null) {
            return true;
        }
        return ObjectUtil.equal(target.getDeptId(), dept.getDeptId());
    }

    @Override
    public List<String> selectRoleDeptTree(Long roleId) {
        return sysDeptMapper.selectRoleDeptTree(roleId);
    }

    @Override
    public int updateDeptStatusNormal(Long[] deptIds) {
        return sysDeptMapper.updateDeptStatusNormal(deptIds);
    }

    @Override
    public List<SysDept> selectChildrenDeptById(Long deptId) {
        List<SysDeptDO> list = sysDeptMapper.selectChildrenDeptById(deptId);
        return ListUtil.convert(list, SysDeptConvertor::toModel);
    }

    @Override
    public int selectNormalChildrenDeptById(Long deptId) {
        return sysDeptMapper.selectNormalChildrenDeptById(deptId);
    }

    @Override
    public int updateDeptSort(SysDept dept) {
        return sysDeptMapper.updateDeptSort(SysDeptConvertor.toDO(dept));
    }
}
