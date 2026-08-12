package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.model.domain.SysUserPost;
import com.jakt.aiplatform.core.model.param.SysUserPostQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.repository.SysUserPostRepository;
import com.jakt.aiplatform.core.service.SysUserPostService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户岗位关联领域服务实现：承载用户岗位关联相关的业务规则。只写规则，不碰持久化细节。
 */
@Service
public class SysUserPostServiceImpl implements SysUserPostService {

    /** 用户岗位关联仓储。 */
    private final SysUserPostRepository sysUserPostRepository;

    public SysUserPostServiceImpl(SysUserPostRepository sysUserPostRepository) {
        this.sysUserPostRepository = sysUserPostRepository;
    }

    @Override
    public SysUserPost createSysUserPost(SysUserPost sysUserPost) {
        return sysUserPostRepository.insert(sysUserPost);
    }

    @Override
    public void updateSysUserPost(SysUserPost sysUserPost) {
        sysUserPostRepository.update(sysUserPost);
    }

    @Override
    public void updateByCondition(SysUserPost sysUserPost) {
        sysUserPostRepository.updateByCondition(sysUserPost);
    }

    @Override
    public void deleteSysUserPost(Long id) {
        sysUserPostRepository.deleteById(id);
    }

    @Override
    public SysUserPost getSysUserPost(Long id) {
        return sysUserPostRepository.findById(id);
    }

    @Override
    public PageResult<SysUserPost> findPage(SysUserPostQueryParam query) {
        return sysUserPostRepository.findPage(query);
    }

    @Override
    public List<SysUserPost> findList(SysUserPostQueryParam query) {
        return sysUserPostRepository.findList(query);
    }
}
