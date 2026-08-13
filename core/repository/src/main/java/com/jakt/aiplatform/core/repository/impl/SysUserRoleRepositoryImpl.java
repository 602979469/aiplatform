package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysUserRoleDO;
import com.jakt.aiplatform.common.dal.mapper.SysUserRoleMapper;
import com.jakt.aiplatform.common.util.tools.ListUtil;
import com.jakt.aiplatform.core.model.domain.SysUserRole;
import com.jakt.aiplatform.core.repository.SysUserRoleRepository;
import com.jakt.aiplatform.core.repository.convertor.SysUserRoleConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户角色关联仓储实现（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
@Repository
public class SysUserRoleRepositoryImpl implements SysUserRoleRepository {

    /** 用户角色关联 Mapper。 */
    private final SysUserRoleMapper sysUserRoleMapper;

    public SysUserRoleRepositoryImpl(SysUserRoleMapper sysUserRoleMapper) {
        this.sysUserRoleMapper = sysUserRoleMapper;
    }

    @Override
    public List<SysUserRole> selectUserRoleByUserId(Long userId) {
        List<SysUserRoleDO> list = sysUserRoleMapper.selectUserRoleByUserId(userId);
        return ListUtil.convert(list, SysUserRoleConvertor::toModel);
    }

    @Override
    public int deleteUserRoleByUserId(Long userId) {
        return sysUserRoleMapper.deleteUserRoleByUserId(userId);
    }

    @Override
    public int deleteUserRole(Long[] ids) {
        return sysUserRoleMapper.deleteUserRole(ids);
    }

    @Override
    public int countUserRoleByRoleId(Long roleId) {
        return sysUserRoleMapper.countUserRoleByRoleId(roleId);
    }

    @Override
    public int batchUserRole(List<SysUserRole> userRoleList) {
        List<SysUserRoleDO> doList = ListUtil.convert(userRoleList, SysUserRoleConvertor::toDO);
        return sysUserRoleMapper.batchUserRole(doList);
    }

    @Override
    public int deleteUserRoleInfo(SysUserRole userRole) {
        return sysUserRoleMapper.deleteUserRoleInfo(SysUserRoleConvertor.toDO(userRole));
    }

    @Override
    public int deleteUserRoleInfos(Long roleId, Long[] userIds) {
        return sysUserRoleMapper.deleteUserRoleInfos(roleId, userIds);
    }
}
