package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysOperLogDO;
import com.jakt.aiplatform.common.dal.mapper.SysOperLogMapper;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.SysOperLog;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysOperLogQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.repository.SysOperLogRepository;
import com.jakt.aiplatform.core.repository.convertor.SysOperLogConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 操作日志仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
@Repository
public class SysOperLogRepositoryImpl implements SysOperLogRepository {

    /** 操作日志 Mapper。 */
    private final SysOperLogMapper sysOperLogMapper;

    public SysOperLogRepositoryImpl(SysOperLogMapper sysOperLogMapper) {
        this.sysOperLogMapper = sysOperLogMapper;
    }

    @Override
    public SysOperLog findById(Long id) {
        return SysOperLogConvertor.toModel(sysOperLogMapper.selectById(id));
    }

    @Override
    public List<SysOperLog> findList(SysOperLogQueryParam query) {
        return sysOperLogMapper.selectList(query).stream().map(SysOperLogConvertor::toModel).toList();
    }

    @Override
    public PageResult<SysOperLog> findPage(SysOperLogQueryParam query) {
        List<SysOperLogDO> doList = sysOperLogMapper.selectPage(query);
        long total = sysOperLogMapper.countByQuery(query);
        List<SysOperLog> list = doList.stream().map(SysOperLogConvertor::toModel).toList();
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public SysOperLog insert(SysOperLog sysOperLog) {
        SysOperLogDO sysOperLogDO = SysOperLogConvertor.toDO(sysOperLog);
        sysOperLogMapper.insert(sysOperLogDO);
        return SysOperLogConvertor.toModel(sysOperLogDO);
    }

    @Override
    public void update(SysOperLog sysOperLog) {
        SysOperLogDO sysOperLogDO = SysOperLogConvertor.toDO(sysOperLog);
        int affected = sysOperLogMapper.update(sysOperLogDO);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysOperLogRepository.update operId={} 影响行数={}", sysOperLog.getOperId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void updateByCondition(SysOperLog sysOperLog) {
        int affected = sysOperLogMapper.updateByCondition(SysOperLogConvertor.toDO(sysOperLog));
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysOperLogRepository.updateByCondition operId={} 影响行数={}", sysOperLog.getOperId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void deleteById(Long id) {
        int affected = sysOperLogMapper.deleteById(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysOperLogRepository.deleteById id={} 影响行数={}", id, affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.DELETE_FAILED, "删除失败：记录不存在或已被删除");
    }
}
