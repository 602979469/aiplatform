package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysUserPostDO;
import com.jakt.aiplatform.common.dal.mapper.SysUserPostMapper;
import com.jakt.aiplatform.common.util.tools.ListUtil;
import com.jakt.aiplatform.core.model.domain.SysUserPost;
import com.jakt.aiplatform.core.repository.SysUserPostRepository;
import com.jakt.aiplatform.core.repository.convertor.SysUserPostConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户岗位关联仓储实现（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
@Repository
public class SysUserPostRepositoryImpl implements SysUserPostRepository {

    /** 用户岗位关联 Mapper。 */
    private final SysUserPostMapper sysUserPostMapper;

    public SysUserPostRepositoryImpl(SysUserPostMapper sysUserPostMapper) {
        this.sysUserPostMapper = sysUserPostMapper;
    }

    @Override
    public int deleteUserPostByUserId(Long userId) {
        return sysUserPostMapper.deleteUserPostByUserId(userId);
    }

    @Override
    public int countUserPostById(Long postId) {
        return sysUserPostMapper.countUserPostById(postId);
    }

    @Override
    public int deleteUserPost(Long[] ids) {
        return sysUserPostMapper.deleteUserPost(ids);
    }

    @Override
    public int batchUserPost(List<SysUserPost> userPostList) {
        List<SysUserPostDO> doList = ListUtil.convert(userPostList, SysUserPostConvertor::toDO);
        return sysUserPostMapper.batchUserPost(doList);
    }
}
