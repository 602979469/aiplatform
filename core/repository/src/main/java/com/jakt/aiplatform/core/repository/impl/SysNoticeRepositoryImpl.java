package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysNoticeDO;
import com.jakt.aiplatform.common.dal.mapper.SysNoticeMapper;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.common.util.tools.ListUtil;
import com.jakt.aiplatform.core.model.domain.SysNotice;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.param.SysNoticeQueryParam;
import com.jakt.aiplatform.core.repository.SysNoticeRepository;
import com.jakt.aiplatform.core.repository.convertor.SysNoticeConvertor;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 通知公告仓储实现（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
@Repository
public class SysNoticeRepositoryImpl implements SysNoticeRepository {

    /** 通知公告 Mapper。 */
    private final SysNoticeMapper sysNoticeMapper;

    public SysNoticeRepositoryImpl(SysNoticeMapper sysNoticeMapper) {
        this.sysNoticeMapper = sysNoticeMapper;
    }

    private SysNotice findOne(SysNoticeDO sysNoticeDO) {
        SysNoticeQueryParam query = SysNoticeConvertor.toQueryParam(sysNoticeDO);
        List<SysNoticeDO> list = sysNoticeMapper.selectList(query);
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        AiPlatformInvoker.throwErrWhenTrue(list.size() > 1, ErrorCodeEnum.RESULT_NOT_UNIQUE, "查询结果不唯一");
        return SysNoticeConvertor.toModel(list.get(0));
    }

    @Override
    public List<SysNotice> selectNoticeList(SysNotice notice) {
        List<SysNoticeDO> list = sysNoticeMapper.selectList(SysNoticeConvertor.toQueryParam(notice));
        return ListUtil.convert(list, SysNoticeConvertor::toModel);
    }

    @Override
    public SysNotice selectNoticeById(Long noticeId) {
        return SysNoticeConvertor.toModel(sysNoticeMapper.selectById(noticeId));
    }

    @Override
    public int insertNotice(SysNotice notice) {
        return sysNoticeMapper.insert(SysNoticeConvertor.toDO(notice));
    }

    @Override
    public int updateNotice(SysNotice notice) {
        return sysNoticeMapper.update(SysNoticeConvertor.toDO(notice));
    }

    @Override
    public int deleteNoticeByIds(String ids) {
        return sysNoticeMapper.deleteByIds(Convert.toLongArray(ids));
    }
}
