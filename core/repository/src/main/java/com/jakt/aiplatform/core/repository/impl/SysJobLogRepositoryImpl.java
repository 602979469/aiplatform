package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysJobLogDO;
import com.jakt.aiplatform.common.dal.mapper.SysJobLogMapper;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.common.util.tools.ListUtil;
import com.jakt.aiplatform.core.model.domain.SysJobLog;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.param.SysJobLogQueryParam;
import com.jakt.aiplatform.core.repository.SysJobLogRepository;
import com.jakt.aiplatform.core.repository.convertor.SysJobLogConvertor;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 定时任务日志仓储实现（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
@Repository
public class SysJobLogRepositoryImpl implements SysJobLogRepository {

    /** 定时任务日志 Mapper。 */
    private final SysJobLogMapper sysJobLogMapper;

    public SysJobLogRepositoryImpl(SysJobLogMapper sysJobLogMapper) {
        this.sysJobLogMapper = sysJobLogMapper;
    }

    /** 按条件取单条：空返回 null，多条抛 RESULT_NOT_UNIQUE。 */
    private SysJobLog findOne(SysJobLogDO condition) {
        List<SysJobLogDO> list = sysJobLogMapper.selectList(SysJobLogConvertor.toQueryParam(condition));
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        AiPlatformInvoker.throwErrWhenTrue(list.size() > 1, ErrorCodeEnum.RESULT_NOT_UNIQUE, "查询结果不唯一");
        return SysJobLogConvertor.toModel(list.get(0));
    }

    @Override
    public List<SysJobLog> selectJobLogList(SysJobLog jobLog) {
        List<SysJobLogDO> list = sysJobLogMapper.selectList(SysJobLogConvertor.toQueryParam(jobLog));
        return ListUtil.convert(list, SysJobLogConvertor::toModel);
    }

    @Override
    public List<SysJobLog> selectJobLogAll() {
        List<SysJobLogDO> list = sysJobLogMapper.selectList(new SysJobLogQueryParam());
        return ListUtil.convert(list, SysJobLogConvertor::toModel);
    }

    @Override
    public SysJobLog selectJobLogById(Long jobLogId) {
        SysJobLogDO condition = new SysJobLogDO();
        condition.setJobLogId(jobLogId);
        return findOne(condition);
    }

    @Override
    public int insertJobLog(SysJobLog jobLog) {
        return sysJobLogMapper.insert(SysJobLogConvertor.toDO(jobLog));
    }

    @Override
    public int deleteJobLogByIds(String ids) {
        return sysJobLogMapper.deleteByIds(Convert.toLongArray(ids));
    }

    @Override
    public int deleteJobLogById(Long jobLogId) {
        return sysJobLogMapper.deleteById(jobLogId);
    }

    @Override
    public int cleanJobLog() {
        return sysJobLogMapper.cleanJobLog();
    }
}
