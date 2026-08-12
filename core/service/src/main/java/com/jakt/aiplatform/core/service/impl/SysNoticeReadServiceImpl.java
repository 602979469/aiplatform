package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.model.domain.SysNoticeRead;
import com.jakt.aiplatform.core.model.param.SysNoticeReadQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.repository.SysNoticeReadRepository;
import com.jakt.aiplatform.core.service.SysNoticeReadService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 公告已读记录领域服务实现：承载公告已读记录相关的业务规则。只写规则，不碰持久化细节。
 */
@Service
public class SysNoticeReadServiceImpl implements SysNoticeReadService {

    /** 公告已读记录仓储。 */
    private final SysNoticeReadRepository sysNoticeReadRepository;

    public SysNoticeReadServiceImpl(SysNoticeReadRepository sysNoticeReadRepository) {
        this.sysNoticeReadRepository = sysNoticeReadRepository;
    }

    @Override
    public SysNoticeRead createSysNoticeRead(SysNoticeRead sysNoticeRead) {
        return sysNoticeReadRepository.insert(sysNoticeRead);
    }

    @Override
    public void updateSysNoticeRead(SysNoticeRead sysNoticeRead) {
        sysNoticeReadRepository.update(sysNoticeRead);
    }

    @Override
    public void updateByCondition(SysNoticeRead sysNoticeRead) {
        sysNoticeReadRepository.updateByCondition(sysNoticeRead);
    }

    @Override
    public void deleteSysNoticeRead(Long id) {
        sysNoticeReadRepository.deleteById(id);
    }

    @Override
    public SysNoticeRead getSysNoticeRead(Long id) {
        return sysNoticeReadRepository.findById(id);
    }

    @Override
    public PageResult<SysNoticeRead> findPage(SysNoticeReadQueryParam query) {
        return sysNoticeReadRepository.findPage(query);
    }

    @Override
    public List<SysNoticeRead> findList(SysNoticeReadQueryParam query) {
        return sysNoticeReadRepository.findList(query);
    }
}
