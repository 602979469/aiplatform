package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysUserOnlineDO;
import com.jakt.aiplatform.common.dal.mapper.SysUserOnlineMapper;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.SysUserOnline;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysUserOnlineQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.repository.SysUserOnlineRepository;
import com.jakt.aiplatform.core.repository.convertor.SysUserOnlineConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 在线用户仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
@Repository
public class SysUserOnlineRepositoryImpl implements SysUserOnlineRepository {

    /** 在线用户 Mapper。 */
    private final SysUserOnlineMapper sysUserOnlineMapper;

    public SysUserOnlineRepositoryImpl(SysUserOnlineMapper sysUserOnlineMapper) {
        this.sysUserOnlineMapper = sysUserOnlineMapper;
    }

    @Override
    public SysUserOnline findById(String id) {
        return SysUserOnlineConvertor.toModel(sysUserOnlineMapper.selectById(id));
    }

    @Override
    public List<SysUserOnline> findList(SysUserOnlineQueryParam query) {
        return sysUserOnlineMapper.selectList(query).stream().map(SysUserOnlineConvertor::toModel).toList();
    }

    @Override
    public PageResult<SysUserOnline> findPage(SysUserOnlineQueryParam query) {
        List<SysUserOnlineDO> doList = sysUserOnlineMapper.selectPage(query);
        long total = sysUserOnlineMapper.countByQuery(query);
        List<SysUserOnline> list = doList.stream().map(SysUserOnlineConvertor::toModel).toList();
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public SysUserOnline insert(SysUserOnline sysUserOnline) {
        SysUserOnlineDO sysUserOnlineDO = SysUserOnlineConvertor.toDO(sysUserOnline);
        sysUserOnlineMapper.insert(sysUserOnlineDO);
        return SysUserOnlineConvertor.toModel(sysUserOnlineDO);
    }

    @Override
    public void update(SysUserOnline sysUserOnline) {
        SysUserOnlineDO sysUserOnlineDO = SysUserOnlineConvertor.toDO(sysUserOnline);
        int affected = sysUserOnlineMapper.update(sysUserOnlineDO);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysUserOnlineRepository.update sessionId={} 影响行数={}", sysUserOnline.getSessionId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void updateByCondition(SysUserOnline sysUserOnline) {
        int affected = sysUserOnlineMapper.updateByCondition(SysUserOnlineConvertor.toDO(sysUserOnline));
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysUserOnlineRepository.updateByCondition sessionId={} 影响行数={}", sysUserOnline.getSessionId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void deleteById(String id) {
        int affected = sysUserOnlineMapper.deleteById(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysUserOnlineRepository.deleteById id={} 影响行数={}", id, affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.DELETE_FAILED, "删除失败：记录不存在或已被删除");
    }
}
