package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysJobLogDO;
import com.jakt.aiplatform.common.dal.mapper.SysJobLogMapper;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.SysJobLog;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysJobLogQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.repository.SysJobLogRepository;
import com.jakt.aiplatform.core.repository.convertor.SysJobLogConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 定时任务日志仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
@Repository
public class SysJobLogRepositoryImpl implements SysJobLogRepository {

    /** 定时任务日志 Mapper。 */
    private final SysJobLogMapper sysJobLogMapper;

    public SysJobLogRepositoryImpl(SysJobLogMapper sysJobLogMapper) {
        this.sysJobLogMapper = sysJobLogMapper;
    }

    @Override
    public SysJobLog findById(Long id) {
        return SysJobLogConvertor.toModel(sysJobLogMapper.selectById(id));
    }

    @Override
    public List<SysJobLog> findList(SysJobLogQueryParam query) {
        return sysJobLogMapper.selectList(query).stream().map(SysJobLogConvertor::toModel).toList();
    }

    @Override
    public PageResult<SysJobLog> findPage(SysJobLogQueryParam query) {
        List<SysJobLogDO> doList = sysJobLogMapper.selectPage(query);
        long total = sysJobLogMapper.countByQuery(query);
        List<SysJobLog> list = doList.stream().map(SysJobLogConvertor::toModel).toList();
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public SysJobLog insert(SysJobLog sysJobLog) {
        SysJobLogDO sysJobLogDO = SysJobLogConvertor.toDO(sysJobLog);
        sysJobLogMapper.insert(sysJobLogDO);
        return SysJobLogConvertor.toModel(sysJobLogDO);
    }

    @Override
    public void update(SysJobLog sysJobLog) {
        SysJobLogDO sysJobLogDO = SysJobLogConvertor.toDO(sysJobLog);
        int affected = sysJobLogMapper.update(sysJobLogDO);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysJobLogRepository.update jobLogId={} 影响行数={}", sysJobLog.getJobLogId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void updateByCondition(SysJobLog sysJobLog) {
        int affected = sysJobLogMapper.updateByCondition(SysJobLogConvertor.toDO(sysJobLog));
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysJobLogRepository.updateByCondition jobLogId={} 影响行数={}", sysJobLog.getJobLogId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void deleteById(Long id) {
        int affected = sysJobLogMapper.deleteById(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysJobLogRepository.deleteById id={} 影响行数={}", id, affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.DELETE_FAILED, "删除失败：记录不存在或已被删除");
    }
}
