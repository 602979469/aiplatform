package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysNoticeReadDO;
import com.jakt.aiplatform.common.dal.mapper.SysNoticeReadMapper;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.SysNoticeRead;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysNoticeReadQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.repository.SysNoticeReadRepository;
import com.jakt.aiplatform.core.repository.convertor.SysNoticeReadConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 公告已读记录仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
@Repository
public class SysNoticeReadRepositoryImpl implements SysNoticeReadRepository {

    /** 公告已读记录 Mapper。 */
    private final SysNoticeReadMapper sysNoticeReadMapper;

    public SysNoticeReadRepositoryImpl(SysNoticeReadMapper sysNoticeReadMapper) {
        this.sysNoticeReadMapper = sysNoticeReadMapper;
    }

    @Override
    public SysNoticeRead findById(Long id) {
        return SysNoticeReadConvertor.toModel(sysNoticeReadMapper.selectById(id));
    }

    @Override
    public List<SysNoticeRead> findList(SysNoticeReadQueryParam query) {
        return sysNoticeReadMapper.selectList(query).stream().map(SysNoticeReadConvertor::toModel).toList();
    }

    @Override
    public PageResult<SysNoticeRead> findPage(SysNoticeReadQueryParam query) {
        List<SysNoticeReadDO> doList = sysNoticeReadMapper.selectPage(query);
        long total = sysNoticeReadMapper.countByQuery(query);
        List<SysNoticeRead> list = doList.stream().map(SysNoticeReadConvertor::toModel).toList();
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public SysNoticeRead insert(SysNoticeRead sysNoticeRead) {
        SysNoticeReadDO sysNoticeReadDO = SysNoticeReadConvertor.toDO(sysNoticeRead);
        sysNoticeReadMapper.insert(sysNoticeReadDO);
        return SysNoticeReadConvertor.toModel(sysNoticeReadDO);
    }

    @Override
    public void update(SysNoticeRead sysNoticeRead) {
        SysNoticeReadDO sysNoticeReadDO = SysNoticeReadConvertor.toDO(sysNoticeRead);
        int affected = sysNoticeReadMapper.update(sysNoticeReadDO);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysNoticeReadRepository.update readId={} 影响行数={}", sysNoticeRead.getReadId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void updateByCondition(SysNoticeRead sysNoticeRead) {
        int affected = sysNoticeReadMapper.updateByCondition(SysNoticeReadConvertor.toDO(sysNoticeRead));
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysNoticeReadRepository.updateByCondition readId={} 影响行数={}", sysNoticeRead.getReadId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void deleteById(Long id) {
        int affected = sysNoticeReadMapper.deleteById(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysNoticeReadRepository.deleteById id={} 影响行数={}", id, affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.DELETE_FAILED, "删除失败：记录不存在或已被删除");
    }
}
