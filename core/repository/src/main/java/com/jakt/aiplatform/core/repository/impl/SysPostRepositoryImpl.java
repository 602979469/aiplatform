package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysPostDO;
import com.jakt.aiplatform.common.dal.mapper.SysPostMapper;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.SysPost;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysPostQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.repository.SysPostRepository;
import com.jakt.aiplatform.core.repository.convertor.SysPostConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 岗位仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
@Repository
public class SysPostRepositoryImpl implements SysPostRepository {

    /** 岗位 Mapper。 */
    private final SysPostMapper sysPostMapper;

    public SysPostRepositoryImpl(SysPostMapper sysPostMapper) {
        this.sysPostMapper = sysPostMapper;
    }

    @Override
    public SysPost findById(Long id) {
        return SysPostConvertor.toModel(sysPostMapper.selectById(id));
    }

    @Override
    public List<SysPost> findList(SysPostQueryParam query) {
        return sysPostMapper.selectList(query).stream().map(SysPostConvertor::toModel).toList();
    }

    @Override
    public PageResult<SysPost> findPage(SysPostQueryParam query) {
        List<SysPostDO> doList = sysPostMapper.selectPage(query);
        long total = sysPostMapper.countByQuery(query);
        List<SysPost> list = doList.stream().map(SysPostConvertor::toModel).toList();
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public SysPost insert(SysPost sysPost) {
        SysPostDO sysPostDO = SysPostConvertor.toDO(sysPost);
        sysPostMapper.insert(sysPostDO);
        return SysPostConvertor.toModel(sysPostDO);
    }

    @Override
    public void update(SysPost sysPost) {
        SysPostDO sysPostDO = SysPostConvertor.toDO(sysPost);
        int affected = sysPostMapper.update(sysPostDO);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysPostRepository.update postId={} 影响行数={}", sysPost.getPostId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void updateByCondition(SysPost sysPost) {
        int affected = sysPostMapper.updateByCondition(SysPostConvertor.toDO(sysPost));
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysPostRepository.updateByCondition postId={} 影响行数={}", sysPost.getPostId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void deleteById(Long id) {
        int affected = sysPostMapper.deleteById(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysPostRepository.deleteById id={} 影响行数={}", id, affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.DELETE_FAILED, "删除失败：记录不存在或已被删除");
    }
}
