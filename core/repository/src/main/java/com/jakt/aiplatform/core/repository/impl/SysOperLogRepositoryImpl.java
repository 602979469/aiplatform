package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.mapper.SysOperLogMapper;
import com.jakt.aiplatform.common.dal.dataobject.SysOperLogDO;
import com.jakt.aiplatform.common.util.tools.ListUtil;
import com.jakt.aiplatform.core.model.domain.SysOperLog;
import com.jakt.aiplatform.core.model.param.SysOperLogQueryParam;
import com.jakt.aiplatform.core.repository.SysOperLogRepository;
import com.jakt.aiplatform.core.repository.convertor.SysOperLogConvertor;
import cn.hutool.core.convert.Convert;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 操作日志仓储实现（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
@Repository
public class SysOperLogRepositoryImpl implements SysOperLogRepository {

    /** 操作日志 Mapper。 */
    private final SysOperLogMapper sysOperLogMapper;

    public SysOperLogRepositoryImpl(SysOperLogMapper sysOperLogMapper) {
        this.sysOperLogMapper = sysOperLogMapper;
    }

    @Override
    public int insertOperlog(SysOperLog operLog) {
        return sysOperLogMapper.insert(SysOperLogConvertor.toDO(operLog));
    }

    @Override
    public List<SysOperLog> selectOperLogList(SysOperLog operLog) {
        List<SysOperLogDO> list = sysOperLogMapper.selectList(SysOperLogConvertor.toQueryParam(operLog));
        return ListUtil.convert(list, SysOperLogConvertor::toModel);
    }

    @Override
    public int deleteOperLogByIds(String ids) {
        return sysOperLogMapper.deleteByIds(Convert.toLongArray(ids));
    }

    @Override
    public SysOperLog selectOperLogById(Long operId) {
        return SysOperLogConvertor.toModel(sysOperLogMapper.selectById(operId));
    }

    @Override
    public int cleanOperLog() {
        return sysOperLogMapper.cleanOperLog();
    }
}
