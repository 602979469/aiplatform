package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysDictDataDO;
import com.jakt.aiplatform.common.dal.mapper.SysDictDataMapper;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.SysDictData;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysDictDataQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.repository.SysDictDataRepository;
import com.jakt.aiplatform.core.repository.convertor.SysDictDataConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 字典数据仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
@Repository
public class SysDictDataRepositoryImpl implements SysDictDataRepository {

    /** 字典数据 Mapper。 */
    private final SysDictDataMapper sysDictDataMapper;

    public SysDictDataRepositoryImpl(SysDictDataMapper sysDictDataMapper) {
        this.sysDictDataMapper = sysDictDataMapper;
    }

    @Override
    public SysDictData findById(Long id) {
        return SysDictDataConvertor.toModel(sysDictDataMapper.selectById(id));
    }

    @Override
    public List<SysDictData> findList(SysDictDataQueryParam query) {
        return sysDictDataMapper.selectList(query).stream().map(SysDictDataConvertor::toModel).toList();
    }

    @Override
    public PageResult<SysDictData> findPage(SysDictDataQueryParam query) {
        List<SysDictDataDO> doList = sysDictDataMapper.selectPage(query);
        long total = sysDictDataMapper.countByQuery(query);
        List<SysDictData> list = doList.stream().map(SysDictDataConvertor::toModel).toList();
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public SysDictData insert(SysDictData sysDictData) {
        SysDictDataDO sysDictDataDO = SysDictDataConvertor.toDO(sysDictData);
        sysDictDataMapper.insert(sysDictDataDO);
        return SysDictDataConvertor.toModel(sysDictDataDO);
    }

    @Override
    public void update(SysDictData sysDictData) {
        SysDictDataDO sysDictDataDO = SysDictDataConvertor.toDO(sysDictData);
        int affected = sysDictDataMapper.update(sysDictDataDO);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysDictDataRepository.update dictCode={} 影响行数={}", sysDictData.getDictCode(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void updateByCondition(SysDictData sysDictData) {
        int affected = sysDictDataMapper.updateByCondition(SysDictDataConvertor.toDO(sysDictData));
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysDictDataRepository.updateByCondition dictCode={} 影响行数={}", sysDictData.getDictCode(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void deleteById(Long id) {
        int affected = sysDictDataMapper.deleteById(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysDictDataRepository.deleteById id={} 影响行数={}", id, affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.DELETE_FAILED, "删除失败：记录不存在或已被删除");
    }
}
