package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.mapper.SysNoticeReadMapper;
import com.jakt.aiplatform.common.util.tools.ListUtil;
import com.jakt.aiplatform.core.model.domain.SysNotice;
import com.jakt.aiplatform.core.model.domain.SysNoticeRead;
import com.jakt.aiplatform.core.model.result.SysReadUserResult;
import com.jakt.aiplatform.core.model.result.SysNoticeListResult;
import com.jakt.aiplatform.core.repository.SysNoticeReadRepository;
import com.jakt.aiplatform.core.repository.convertor.SysNoticeConvertor;
import com.jakt.aiplatform.core.repository.convertor.SysNoticeReadConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 公告已读记录仓储实现（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
@Repository
public class SysNoticeReadRepositoryImpl implements SysNoticeReadRepository {

    /** 公告已读记录 Mapper。 */
    private final SysNoticeReadMapper sysNoticeReadMapper;

    public SysNoticeReadRepositoryImpl(SysNoticeReadMapper sysNoticeReadMapper) {
        this.sysNoticeReadMapper = sysNoticeReadMapper;
    }

    @Override
    public int insertNoticeRead(SysNoticeRead noticeRead) {
        return sysNoticeReadMapper.insertNoticeRead(SysNoticeReadConvertor.toDO(noticeRead));
    }

    @Override
    public int selectUnreadCount(Long userId) {
        return sysNoticeReadMapper.selectUnreadCount(userId);
    }

    @Override
    public int selectIsRead(Long noticeId, Long userId) {
        return sysNoticeReadMapper.selectIsRead(noticeId, userId);
    }

    @Override
    public int insertNoticeReadBatch(Long userId, Long[] noticeIds) {
        return sysNoticeReadMapper.insertNoticeReadBatch(userId, noticeIds);
    }

    @Override
    public List<SysNotice> selectNoticeListWithReadStatus(Long userId, int limit) {
        List<SysNoticeListResult> list = sysNoticeReadMapper.selectNoticeListWithReadStatus(userId, limit);
        return ListUtil.convert(list, SysNoticeConvertor::toModel);
    }

    @Override
    public int deleteByNoticeIds(String[] noticeIds) {
        return sysNoticeReadMapper.deleteByNoticeIds(noticeIds);
    }

    @Override
    public List<SysReadUserResult> selectReadUsersByNoticeId(Long noticeId, String searchValue) {
        return sysNoticeReadMapper.selectReadUsersByNoticeId(noticeId, searchValue);
    }
}
