package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysRoleDeptDO;
import com.jakt.aiplatform.common.dal.mapper.SysRoleDeptMapper;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.SysRoleDept;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysRoleDeptQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.repository.SysRoleDeptRepository;
import com.jakt.aiplatform.core.repository.convertor.SysRoleDeptConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色部门关联仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
@Repository
public class SysRoleDeptRepositoryImpl implements SysRoleDeptRepository {

    /** 角色部门关联 Mapper。 */
    private final SysRoleDeptMapper sysRoleDeptMapper;

    public SysRoleDeptRepositoryImpl(SysRoleDeptMapper sysRoleDeptMapper) {
        this.sysRoleDeptMapper = sysRoleDeptMapper;
    }

    @Override
    public SysRoleDept findById(Long id) {
        return SysRoleDeptConvertor.toModel(sysRoleDeptMapper.selectById(id));
    }

    @Override
    public List<SysRoleDept> findList(SysRoleDeptQueryParam query) {
        return sysRoleDeptMapper.selectList(query).stream().map(SysRoleDeptConvertor::toModel).toList();
    }

    @Override
    public PageResult<SysRoleDept> findPage(SysRoleDeptQueryParam query) {
        List<SysRoleDeptDO> doList = sysRoleDeptMapper.selectPage(query);
        long total = sysRoleDeptMapper.countByQuery(query);
        List<SysRoleDept> list = doList.stream().map(SysRoleDeptConvertor::toModel).toList();
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public SysRoleDept insert(SysRoleDept sysRoleDept) {
        SysRoleDeptDO sysRoleDeptDO = SysRoleDeptConvertor.toDO(sysRoleDept);
        sysRoleDeptMapper.insert(sysRoleDeptDO);
        return SysRoleDeptConvertor.toModel(sysRoleDeptDO);
    }

    @Override
    public void update(SysRoleDept sysRoleDept) {
        SysRoleDeptDO sysRoleDeptDO = SysRoleDeptConvertor.toDO(sysRoleDept);
        int affected = sysRoleDeptMapper.update(sysRoleDeptDO);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysRoleDeptRepository.update id={} 影响行数={}", sysRoleDept.getId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void updateByCondition(SysRoleDept sysRoleDept) {
        int affected = sysRoleDeptMapper.updateByCondition(SysRoleDeptConvertor.toDO(sysRoleDept));
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysRoleDeptRepository.updateByCondition id={} 影响行数={}", sysRoleDept.getId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void deleteById(Long id) {
        int affected = sysRoleDeptMapper.deleteById(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysRoleDeptRepository.deleteById id={} 影响行数={}", id, affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.DELETE_FAILED, "删除失败：记录不存在或已被删除");
    }
}
