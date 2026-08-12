package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysDictTypeDO;
import com.jakt.aiplatform.common.dal.mapper.SysDictTypeMapper;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.SysDictType;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysDictTypeQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.repository.SysDictTypeRepository;
import com.jakt.aiplatform.core.repository.convertor.SysDictTypeConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 字典类型仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
@Repository
public class SysDictTypeRepositoryImpl implements SysDictTypeRepository {

    /** 字典类型 Mapper。 */
    private final SysDictTypeMapper sysDictTypeMapper;

    public SysDictTypeRepositoryImpl(SysDictTypeMapper sysDictTypeMapper) {
        this.sysDictTypeMapper = sysDictTypeMapper;
    }

    @Override
    public SysDictType findById(Long id) {
        return SysDictTypeConvertor.toModel(sysDictTypeMapper.selectById(id));
    }

    @Override
    public List<SysDictType> findList(SysDictTypeQueryParam query) {
        return sysDictTypeMapper.selectList(query).stream().map(SysDictTypeConvertor::toModel).toList();
    }

    @Override
    public PageResult<SysDictType> findPage(SysDictTypeQueryParam query) {
        List<SysDictTypeDO> doList = sysDictTypeMapper.selectPage(query);
        long total = sysDictTypeMapper.countByQuery(query);
        List<SysDictType> list = doList.stream().map(SysDictTypeConvertor::toModel).toList();
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public SysDictType insert(SysDictType sysDictType) {
        SysDictTypeDO sysDictTypeDO = SysDictTypeConvertor.toDO(sysDictType);
        sysDictTypeMapper.insert(sysDictTypeDO);
        return SysDictTypeConvertor.toModel(sysDictTypeDO);
    }

    @Override
    public void update(SysDictType sysDictType) {
        SysDictTypeDO sysDictTypeDO = SysDictTypeConvertor.toDO(sysDictType);
        int affected = sysDictTypeMapper.update(sysDictTypeDO);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysDictTypeRepository.update dictId={} 影响行数={}", sysDictType.getDictId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void updateByCondition(SysDictType sysDictType) {
        int affected = sysDictTypeMapper.updateByCondition(SysDictTypeConvertor.toDO(sysDictType));
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysDictTypeRepository.updateByCondition dictId={} 影响行数={}", sysDictType.getDictId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void deleteById(Long id) {
        int affected = sysDictTypeMapper.deleteById(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysDictTypeRepository.deleteById id={} 影响行数={}", id, affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.DELETE_FAILED, "删除失败：记录不存在或已被删除");
    }
}
