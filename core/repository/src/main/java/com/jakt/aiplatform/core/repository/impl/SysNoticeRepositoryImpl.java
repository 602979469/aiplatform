package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysNoticeDO;
import com.jakt.aiplatform.common.dal.mapper.SysNoticeMapper;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.SysNotice;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysNoticeQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.repository.SysNoticeRepository;
import com.jakt.aiplatform.core.repository.convertor.SysNoticeConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 通知公告仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
@Repository
public class SysNoticeRepositoryImpl implements SysNoticeRepository {

    /** 通知公告 Mapper。 */
    private final SysNoticeMapper sysNoticeMapper;

    public SysNoticeRepositoryImpl(SysNoticeMapper sysNoticeMapper) {
        this.sysNoticeMapper = sysNoticeMapper;
    }

    @Override
    public SysNotice findById(Long id) {
        return SysNoticeConvertor.toModel(sysNoticeMapper.selectById(id));
    }

    @Override
    public List<SysNotice> findList(SysNoticeQueryParam query) {
        return sysNoticeMapper.selectList(query).stream().map(SysNoticeConvertor::toModel).toList();
    }

    @Override
    public PageResult<SysNotice> findPage(SysNoticeQueryParam query) {
        List<SysNoticeDO> doList = sysNoticeMapper.selectPage(query);
        long total = sysNoticeMapper.countByQuery(query);
        List<SysNotice> list = doList.stream().map(SysNoticeConvertor::toModel).toList();
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public SysNotice insert(SysNotice sysNotice) {
        SysNoticeDO sysNoticeDO = SysNoticeConvertor.toDO(sysNotice);
        sysNoticeMapper.insert(sysNoticeDO);
        return SysNoticeConvertor.toModel(sysNoticeDO);
    }

    @Override
    public void update(SysNotice sysNotice) {
        SysNoticeDO sysNoticeDO = SysNoticeConvertor.toDO(sysNotice);
        int affected = sysNoticeMapper.update(sysNoticeDO);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysNoticeRepository.update noticeId={} 影响行数={}", sysNotice.getNoticeId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void updateByCondition(SysNotice sysNotice) {
        int affected = sysNoticeMapper.updateByCondition(SysNoticeConvertor.toDO(sysNotice));
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysNoticeRepository.updateByCondition noticeId={} 影响行数={}", sysNotice.getNoticeId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void deleteById(Long id) {
        int affected = sysNoticeMapper.deleteById(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysNoticeRepository.deleteById id={} 影响行数={}", id, affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.DELETE_FAILED, "删除失败：记录不存在或已被删除");
    }
}
