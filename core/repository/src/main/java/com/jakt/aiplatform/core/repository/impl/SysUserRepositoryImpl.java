package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysUserDO;
import com.jakt.aiplatform.common.dal.mapper.SysUserMapper;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.SysUser;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysUserQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.repository.SysUserRepository;
import com.jakt.aiplatform.core.repository.convertor.SysUserConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
@Repository
public class SysUserRepositoryImpl implements SysUserRepository {

    /** 用户 Mapper。 */
    private final SysUserMapper sysUserMapper;

    public SysUserRepositoryImpl(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public SysUser findById(Long id) {
        return SysUserConvertor.toModel(sysUserMapper.selectById(id));
    }

    @Override
    public List<SysUser> findList(SysUserQueryParam query) {
        return sysUserMapper.selectList(query).stream().map(SysUserConvertor::toModel).toList();
    }

    @Override
    public PageResult<SysUser> findPage(SysUserQueryParam query) {
        List<SysUserDO> doList = sysUserMapper.selectPage(query);
        long total = sysUserMapper.countByQuery(query);
        List<SysUser> list = doList.stream().map(SysUserConvertor::toModel).toList();
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public SysUser insert(SysUser sysUser) {
        SysUserDO sysUserDO = SysUserConvertor.toDO(sysUser);
        sysUserMapper.insert(sysUserDO);
        return SysUserConvertor.toModel(sysUserDO);
    }

    @Override
    public void update(SysUser sysUser) {
        SysUserDO sysUserDO = SysUserConvertor.toDO(sysUser);
        int affected = sysUserMapper.update(sysUserDO);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysUserRepository.update userId={} 影响行数={}", sysUser.getUserId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void updateByCondition(SysUser sysUser) {
        int affected = sysUserMapper.updateByCondition(SysUserConvertor.toDO(sysUser));
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysUserRepository.updateByCondition userId={} 影响行数={}", sysUser.getUserId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void deleteById(Long id) {
        int affected = sysUserMapper.deleteById(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysUserRepository.deleteById id={} 影响行数={}", id, affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.DELETE_FAILED, "删除失败：记录不存在或已被删除");
    }
}
