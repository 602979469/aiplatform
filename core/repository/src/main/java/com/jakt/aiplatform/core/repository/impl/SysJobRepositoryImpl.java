package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysJobDO;
import com.jakt.aiplatform.common.dal.mapper.SysJobMapper;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.SysJob;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysJobQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.repository.SysJobRepository;
import com.jakt.aiplatform.core.repository.convertor.SysJobConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 定时任务仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
@Repository
public class SysJobRepositoryImpl implements SysJobRepository {

    /** 定时任务 Mapper。 */
    private final SysJobMapper sysJobMapper;

    public SysJobRepositoryImpl(SysJobMapper sysJobMapper) {
        this.sysJobMapper = sysJobMapper;
    }

    @Override
    public SysJob findById(Long id) {
        return SysJobConvertor.toModel(sysJobMapper.selectById(id));
    }

    @Override
    public List<SysJob> findList(SysJobQueryParam query) {
        return sysJobMapper.selectList(query).stream().map(SysJobConvertor::toModel).toList();
    }

    @Override
    public PageResult<SysJob> findPage(SysJobQueryParam query) {
        List<SysJobDO> doList = sysJobMapper.selectPage(query);
        long total = sysJobMapper.countByQuery(query);
        List<SysJob> list = doList.stream().map(SysJobConvertor::toModel).toList();
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public SysJob insert(SysJob sysJob) {
        SysJobDO sysJobDO = SysJobConvertor.toDO(sysJob);
        sysJobMapper.insert(sysJobDO);
        return SysJobConvertor.toModel(sysJobDO);
    }

    @Override
    public void update(SysJob sysJob) {
        SysJobDO sysJobDO = SysJobConvertor.toDO(sysJob);
        int affected = sysJobMapper.update(sysJobDO);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysJobRepository.update jobId={} 影响行数={}", sysJob.getJobId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void updateByCondition(SysJob sysJob) {
        int affected = sysJobMapper.updateByCondition(SysJobConvertor.toDO(sysJob));
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysJobRepository.updateByCondition jobId={} 影响行数={}", sysJob.getJobId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void deleteById(Long id) {
        int affected = sysJobMapper.deleteById(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysJobRepository.deleteById id={} 影响行数={}", id, affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.DELETE_FAILED, "删除失败：记录不存在或已被删除");
    }
}
