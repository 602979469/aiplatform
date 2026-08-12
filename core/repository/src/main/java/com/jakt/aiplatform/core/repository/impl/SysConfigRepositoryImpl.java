package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysConfigDO;
import com.jakt.aiplatform.common.dal.mapper.SysConfigMapper;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.SysConfig;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysConfigQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.repository.SysConfigRepository;
import com.jakt.aiplatform.core.repository.convertor.SysConfigConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 参数配置仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
@Repository
public class SysConfigRepositoryImpl implements SysConfigRepository {

    /** 参数配置 Mapper。 */
    private final SysConfigMapper sysConfigMapper;

    public SysConfigRepositoryImpl(SysConfigMapper sysConfigMapper) {
        this.sysConfigMapper = sysConfigMapper;
    }

    @Override
    public SysConfig findById(Long id) {
        return SysConfigConvertor.toModel(sysConfigMapper.selectById(id));
    }

    @Override
    public List<SysConfig> findList(SysConfigQueryParam query) {
        return sysConfigMapper.selectList(query).stream().map(SysConfigConvertor::toModel).toList();
    }

    @Override
    public PageResult<SysConfig> findPage(SysConfigQueryParam query) {
        List<SysConfigDO> doList = sysConfigMapper.selectPage(query);
        long total = sysConfigMapper.countByQuery(query);
        List<SysConfig> list = doList.stream().map(SysConfigConvertor::toModel).toList();
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public SysConfig insert(SysConfig sysConfig) {
        SysConfigDO sysConfigDO = SysConfigConvertor.toDO(sysConfig);
        sysConfigMapper.insert(sysConfigDO);
        return SysConfigConvertor.toModel(sysConfigDO);
    }

    @Override
    public void update(SysConfig sysConfig) {
        SysConfigDO sysConfigDO = SysConfigConvertor.toDO(sysConfig);
        int affected = sysConfigMapper.update(sysConfigDO);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysConfigRepository.update configId={} 影响行数={}", sysConfig.getConfigId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void updateByCondition(SysConfig sysConfig) {
        int affected = sysConfigMapper.updateByCondition(SysConfigConvertor.toDO(sysConfig));
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysConfigRepository.updateByCondition configId={} 影响行数={}", sysConfig.getConfigId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void deleteById(Long id) {
        int affected = sysConfigMapper.deleteById(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysConfigRepository.deleteById id={} 影响行数={}", id, affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.DELETE_FAILED, "删除失败：记录不存在或已被删除");
    }
}
