package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysUserRoleDO;
import com.jakt.aiplatform.common.dal.mapper.SysUserRoleMapper;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.SysUserRole;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysUserRoleQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.repository.SysUserRoleRepository;
import com.jakt.aiplatform.core.repository.convertor.SysUserRoleConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户角色关联仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
@Repository
public class SysUserRoleRepositoryImpl implements SysUserRoleRepository {

    /** 用户角色关联 Mapper。 */
    private final SysUserRoleMapper sysUserRoleMapper;

    public SysUserRoleRepositoryImpl(SysUserRoleMapper sysUserRoleMapper) {
        this.sysUserRoleMapper = sysUserRoleMapper;
    }

    @Override
    public SysUserRole findById(Long id) {
        return SysUserRoleConvertor.toModel(sysUserRoleMapper.selectById(id));
    }

    @Override
    public List<SysUserRole> findList(SysUserRoleQueryParam query) {
        return sysUserRoleMapper.selectList(query).stream().map(SysUserRoleConvertor::toModel).toList();
    }

    @Override
    public PageResult<SysUserRole> findPage(SysUserRoleQueryParam query) {
        List<SysUserRoleDO> doList = sysUserRoleMapper.selectPage(query);
        long total = sysUserRoleMapper.countByQuery(query);
        List<SysUserRole> list = doList.stream().map(SysUserRoleConvertor::toModel).toList();
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public SysUserRole insert(SysUserRole sysUserRole) {
        SysUserRoleDO sysUserRoleDO = SysUserRoleConvertor.toDO(sysUserRole);
        sysUserRoleMapper.insert(sysUserRoleDO);
        return SysUserRoleConvertor.toModel(sysUserRoleDO);
    }

    @Override
    public void update(SysUserRole sysUserRole) {
        SysUserRoleDO sysUserRoleDO = SysUserRoleConvertor.toDO(sysUserRole);
        int affected = sysUserRoleMapper.update(sysUserRoleDO);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysUserRoleRepository.update id={} 影响行数={}", sysUserRole.getId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void updateByCondition(SysUserRole sysUserRole) {
        int affected = sysUserRoleMapper.updateByCondition(SysUserRoleConvertor.toDO(sysUserRole));
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysUserRoleRepository.updateByCondition id={} 影响行数={}", sysUserRole.getId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void deleteById(Long id) {
        int affected = sysUserRoleMapper.deleteById(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysUserRoleRepository.deleteById id={} 影响行数={}", id, affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.DELETE_FAILED, "删除失败：记录不存在或已被删除");
    }
}
