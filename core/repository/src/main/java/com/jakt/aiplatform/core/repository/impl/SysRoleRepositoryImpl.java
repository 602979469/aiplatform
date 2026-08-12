package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysRoleDO;
import com.jakt.aiplatform.common.dal.mapper.SysRoleMapper;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.SysRole;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysRoleQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.repository.SysRoleRepository;
import com.jakt.aiplatform.core.repository.convertor.SysRoleConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
@Repository
public class SysRoleRepositoryImpl implements SysRoleRepository {

    /** 角色 Mapper。 */
    private final SysRoleMapper sysRoleMapper;

    public SysRoleRepositoryImpl(SysRoleMapper sysRoleMapper) {
        this.sysRoleMapper = sysRoleMapper;
    }

    @Override
    public SysRole findById(Long id) {
        return SysRoleConvertor.toModel(sysRoleMapper.selectById(id));
    }

    @Override
    public List<SysRole> findList(SysRoleQueryParam query) {
        return sysRoleMapper.selectList(query).stream().map(SysRoleConvertor::toModel).toList();
    }

    @Override
    public PageResult<SysRole> findPage(SysRoleQueryParam query) {
        List<SysRoleDO> doList = sysRoleMapper.selectPage(query);
        long total = sysRoleMapper.countByQuery(query);
        List<SysRole> list = doList.stream().map(SysRoleConvertor::toModel).toList();
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public SysRole insert(SysRole sysRole) {
        SysRoleDO sysRoleDO = SysRoleConvertor.toDO(sysRole);
        sysRoleMapper.insert(sysRoleDO);
        return SysRoleConvertor.toModel(sysRoleDO);
    }

    @Override
    public void update(SysRole sysRole) {
        SysRoleDO sysRoleDO = SysRoleConvertor.toDO(sysRole);
        int affected = sysRoleMapper.update(sysRoleDO);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysRoleRepository.update roleId={} 影响行数={}", sysRole.getRoleId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void updateByCondition(SysRole sysRole) {
        int affected = sysRoleMapper.updateByCondition(SysRoleConvertor.toDO(sysRole));
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysRoleRepository.updateByCondition roleId={} 影响行数={}", sysRole.getRoleId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void deleteById(Long id) {
        int affected = sysRoleMapper.deleteById(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysRoleRepository.deleteById id={} 影响行数={}", id, affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.DELETE_FAILED, "删除失败：记录不存在或已被删除");
    }
}
