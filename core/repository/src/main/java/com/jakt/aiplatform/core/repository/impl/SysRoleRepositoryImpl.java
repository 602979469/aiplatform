package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysRoleDO;
import com.jakt.aiplatform.common.dal.mapper.SysRoleMapper;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.common.util.tools.ListUtil;
import com.jakt.aiplatform.core.model.domain.SysRole;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.param.SysRoleQueryParam;
import com.jakt.aiplatform.core.repository.SysRoleRepository;
import com.jakt.aiplatform.core.repository.convertor.SysRoleConvertor;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色仓储实现（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
@Repository
public class SysRoleRepositoryImpl implements SysRoleRepository {

    /** 角色 Mapper。 */
    private final SysRoleMapper sysRoleMapper;

    public SysRoleRepositoryImpl(SysRoleMapper sysRoleMapper) {
        this.sysRoleMapper = sysRoleMapper;
    }

    /** 按条件取单条：空返回 null，多条抛 RESULT_NOT_UNIQUE。 */
    private SysRole findOne(SysRoleDO condition) {
        List<SysRoleDO> list = sysRoleMapper.selectList(SysRoleConvertor.toQueryParam(condition));
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        AiPlatformInvoker.throwErrWhenTrue(list.size() > 1, ErrorCodeEnum.RESULT_NOT_UNIQUE, "查询结果不唯一");
        return SysRoleConvertor.toModel(list.get(0));
    }

    @Override
    public List<SysRole> selectRoleList(SysRole role) {
        List<SysRoleDO> list = sysRoleMapper.selectList(SysRoleConvertor.toQueryParam(role));
        return ListUtil.convert(list, SysRoleConvertor::toModel);
    }

    @Override
    public List<SysRole> selectRolesByUserId(Long userId) {
        List<SysRoleDO> list = sysRoleMapper.selectRolesByUserId(userId);
        return ListUtil.convert(list, SysRoleConvertor::toModel);
    }

    @Override
    public SysRole selectRoleById(Long roleId) {
        return SysRoleConvertor.toModel(sysRoleMapper.selectById(roleId));
    }

    @Override
    public int deleteRoleById(Long roleId) {
        return sysRoleMapper.deleteById(roleId);
    }

    @Override
    public int deleteRoleByIds(String ids) {
        return sysRoleMapper.deleteByIds(Convert.toLongArray(ids));
    }

    @Override
    public int updateRole(SysRole role) {
        return sysRoleMapper.update(SysRoleConvertor.toDO(role));
    }

    @Override
    public int insertRole(SysRole role) {
        return sysRoleMapper.insert(SysRoleConvertor.toDO(role));
    }

    @Override
    public boolean checkRoleNameUnique(SysRole role) {
        SysRoleQueryParam query = new SysRoleQueryParam();
        query.setRoleName(role.getRoleName());
        List<SysRoleDO> list = sysRoleMapper.selectList(query);
        if (CollUtil.isEmpty(list)) {
            return true;
        }
        if (list.size() > 1) {
            return false;
        }
        return ObjectUtil.equal(list.get(0).getRoleId(), role.getRoleId());
    }

    @Override
    public boolean checkRoleKeyUnique(SysRole role) {
        SysRoleQueryParam query = new SysRoleQueryParam();
        query.setRoleKey(role.getRoleKey());
        List<SysRoleDO> list = sysRoleMapper.selectList(query);
        if (CollUtil.isEmpty(list)) {
            return true;
        }
        if (list.size() > 1) {
            return false;
        }
        return ObjectUtil.equal(list.get(0).getRoleId(), role.getRoleId());
    }
}
