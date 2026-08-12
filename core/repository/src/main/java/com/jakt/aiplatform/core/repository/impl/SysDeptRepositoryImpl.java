package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysDeptDO;
import com.jakt.aiplatform.common.dal.mapper.SysDeptMapper;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.SysDept;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysDeptQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.repository.SysDeptRepository;
import com.jakt.aiplatform.core.repository.convertor.SysDeptConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 部门仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
@Repository
public class SysDeptRepositoryImpl implements SysDeptRepository {

    /** 部门 Mapper。 */
    private final SysDeptMapper sysDeptMapper;

    public SysDeptRepositoryImpl(SysDeptMapper sysDeptMapper) {
        this.sysDeptMapper = sysDeptMapper;
    }

    @Override
    public SysDept findById(Long id) {
        return SysDeptConvertor.toModel(sysDeptMapper.selectById(id));
    }

    @Override
    public List<SysDept> findList(SysDeptQueryParam query) {
        return sysDeptMapper.selectList(query).stream().map(SysDeptConvertor::toModel).toList();
    }

    @Override
    public PageResult<SysDept> findPage(SysDeptQueryParam query) {
        List<SysDeptDO> doList = sysDeptMapper.selectPage(query);
        long total = sysDeptMapper.countByQuery(query);
        List<SysDept> list = doList.stream().map(SysDeptConvertor::toModel).toList();
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public SysDept insert(SysDept sysDept) {
        SysDeptDO sysDeptDO = SysDeptConvertor.toDO(sysDept);
        sysDeptMapper.insert(sysDeptDO);
        return SysDeptConvertor.toModel(sysDeptDO);
    }

    @Override
    public void update(SysDept sysDept) {
        SysDeptDO sysDeptDO = SysDeptConvertor.toDO(sysDept);
        int affected = sysDeptMapper.update(sysDeptDO);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysDeptRepository.update deptId={} 影响行数={}", sysDept.getDeptId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void updateByCondition(SysDept sysDept) {
        int affected = sysDeptMapper.updateByCondition(SysDeptConvertor.toDO(sysDept));
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysDeptRepository.updateByCondition deptId={} 影响行数={}", sysDept.getDeptId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void deleteById(Long id) {
        int affected = sysDeptMapper.deleteById(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysDeptRepository.deleteById id={} 影响行数={}", id, affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.DELETE_FAILED, "删除失败：记录不存在或已被删除");
    }
}
