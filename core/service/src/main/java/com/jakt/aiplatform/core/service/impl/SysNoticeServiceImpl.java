package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.model.domain.SysNotice;
import com.jakt.aiplatform.core.model.param.SysNoticeQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.repository.SysNoticeRepository;
import com.jakt.aiplatform.core.service.SysNoticeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通知公告领域服务实现：承载通知公告相关的业务规则。只写规则，不碰持久化细节。
 */
@Service
public class SysNoticeServiceImpl implements SysNoticeService {

    /** 通知公告仓储。 */
    private final SysNoticeRepository sysNoticeRepository;

    public SysNoticeServiceImpl(SysNoticeRepository sysNoticeRepository) {
        this.sysNoticeRepository = sysNoticeRepository;
    }

    @Override
    public SysNotice createSysNotice(SysNotice sysNotice) {
        return sysNoticeRepository.insert(sysNotice);
    }

    @Override
    public void updateSysNotice(SysNotice sysNotice) {
        sysNoticeRepository.update(sysNotice);
    }

    @Override
    public void updateByCondition(SysNotice sysNotice) {
        sysNoticeRepository.updateByCondition(sysNotice);
    }

    @Override
    public void deleteSysNotice(Long id) {
        sysNoticeRepository.deleteById(id);
    }

    @Override
    public SysNotice getSysNotice(Long id) {
        return sysNoticeRepository.findById(id);
    }

    @Override
    public PageResult<SysNotice> findPage(SysNoticeQueryParam query) {
        return sysNoticeRepository.findPage(query);
    }

    @Override
    public List<SysNotice> findList(SysNoticeQueryParam query) {
        return sysNoticeRepository.findList(query);
    }
}
