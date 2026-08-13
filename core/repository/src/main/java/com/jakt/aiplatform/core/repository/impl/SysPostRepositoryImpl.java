package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysPostDO;
import com.jakt.aiplatform.common.dal.mapper.SysPostMapper;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.common.util.tools.ListUtil;
import com.jakt.aiplatform.core.model.domain.SysPost;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.param.SysPostQueryParam;
import com.jakt.aiplatform.core.repository.SysPostRepository;
import com.jakt.aiplatform.core.repository.convertor.SysPostConvertor;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 岗位仓储实现（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
@Repository
public class SysPostRepositoryImpl implements SysPostRepository {

    /** 岗位 Mapper。 */
    private final SysPostMapper sysPostMapper;

    public SysPostRepositoryImpl(SysPostMapper sysPostMapper) {
        this.sysPostMapper = sysPostMapper;
    }

    private SysPost findOne(SysPostDO sysPostDO) {
        SysPostQueryParam query = SysPostConvertor.toQueryParam(sysPostDO);
        List<SysPostDO> list = sysPostMapper.selectList(query);
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        AiPlatformInvoker.throwErrWhenTrue(list.size() > 1, ErrorCodeEnum.RESULT_NOT_UNIQUE, "查询结果不唯一");
        return SysPostConvertor.toModel(list.get(0));
    }

    @Override
    public List<SysPost> selectPostList(SysPost post) {
        List<SysPostDO> list = sysPostMapper.selectList(SysPostConvertor.toQueryParam(post));
        return ListUtil.convert(list, SysPostConvertor::toModel);
    }

    @Override
    public List<SysPost> selectPostAll() {
        List<SysPostDO> list = sysPostMapper.selectList(new SysPostQueryParam());
        return ListUtil.convert(list, SysPostConvertor::toModel);
    }

    @Override
    public List<SysPost> selectPostsByUserId(Long userId) {
        List<SysPostDO> list = sysPostMapper.selectPostsByUserId(userId);
        return ListUtil.convert(list, SysPostConvertor::toModel);
    }

    @Override
    public SysPost selectPostById(Long postId) {
        return SysPostConvertor.toModel(sysPostMapper.selectById(postId));
    }

    @Override
    public boolean checkPostNameUnique(SysPost post) {
        SysPostQueryParam query = new SysPostQueryParam();
        query.setPostName(post.getPostName());
        List<SysPostDO> list = sysPostMapper.selectList(query);
        if (CollUtil.isEmpty(list)) {
            return true;
        }
        if (list.size() > 1) {
            return false;
        }
        return ObjectUtil.equal(list.get(0).getPostId(), post.getPostId());
    }

    @Override
    public boolean checkPostCodeUnique(SysPost post) {
        SysPostQueryParam query = new SysPostQueryParam();
        query.setPostCode(post.getPostCode());
        List<SysPostDO> list = sysPostMapper.selectList(query);
        if (CollUtil.isEmpty(list)) {
            return true;
        }
        if (list.size() > 1) {
            return false;
        }
        return ObjectUtil.equal(list.get(0).getPostId(), post.getPostId());
    }

    @Override
    public int deletePostByIds(String ids) {
        return sysPostMapper.deleteByIds(Convert.toLongArray(ids));
    }

    @Override
    public int updatePost(SysPost post) {
        return sysPostMapper.update(SysPostConvertor.toDO(post));
    }

    @Override
    public int insertPost(SysPost post) {
        return sysPostMapper.insert(SysPostConvertor.toDO(post));
    }
}
