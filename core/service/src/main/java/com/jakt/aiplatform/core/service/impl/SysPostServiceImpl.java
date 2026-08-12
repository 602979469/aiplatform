package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.model.domain.SysPost;
import com.jakt.aiplatform.core.model.param.SysPostQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.repository.SysPostRepository;
import com.jakt.aiplatform.core.service.SysPostService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 岗位领域服务实现：承载岗位相关的业务规则。只写规则，不碰持久化细节。
 */
@Service
public class SysPostServiceImpl implements SysPostService {

    /** 岗位仓储。 */
    private final SysPostRepository sysPostRepository;

    public SysPostServiceImpl(SysPostRepository sysPostRepository) {
        this.sysPostRepository = sysPostRepository;
    }

    @Override
    public SysPost createSysPost(SysPost sysPost) {
        return sysPostRepository.insert(sysPost);
    }

    @Override
    public void updateSysPost(SysPost sysPost) {
        sysPostRepository.update(sysPost);
    }

    @Override
    public void updateByCondition(SysPost sysPost) {
        sysPostRepository.updateByCondition(sysPost);
    }

    @Override
    public void deleteSysPost(Long id) {
        sysPostRepository.deleteById(id);
    }

    @Override
    public SysPost getSysPost(Long id) {
        return sysPostRepository.findById(id);
    }

    @Override
    public PageResult<SysPost> findPage(SysPostQueryParam query) {
        return sysPostRepository.findPage(query);
    }

    @Override
    public List<SysPost> findList(SysPostQueryParam query) {
        return sysPostRepository.findList(query);
    }
}
