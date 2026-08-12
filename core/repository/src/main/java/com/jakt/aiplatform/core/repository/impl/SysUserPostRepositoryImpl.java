package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysUserPostDO;
import com.jakt.aiplatform.common.dal.mapper.SysUserPostMapper;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.SysUserPost;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysUserPostQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.repository.SysUserPostRepository;
import com.jakt.aiplatform.core.repository.convertor.SysUserPostConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户岗位关联仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
@Repository
public class SysUserPostRepositoryImpl implements SysUserPostRepository {

    /** 用户岗位关联 Mapper。 */
    private final SysUserPostMapper sysUserPostMapper;

    public SysUserPostRepositoryImpl(SysUserPostMapper sysUserPostMapper) {
        this.sysUserPostMapper = sysUserPostMapper;
    }

    @Override
    public SysUserPost findById(Long id) {
        return SysUserPostConvertor.toModel(sysUserPostMapper.selectById(id));
    }

    @Override
    public List<SysUserPost> findList(SysUserPostQueryParam query) {
        return sysUserPostMapper.selectList(query).stream().map(SysUserPostConvertor::toModel).toList();
    }

    @Override
    public PageResult<SysUserPost> findPage(SysUserPostQueryParam query) {
        List<SysUserPostDO> doList = sysUserPostMapper.selectPage(query);
        long total = sysUserPostMapper.countByQuery(query);
        List<SysUserPost> list = doList.stream().map(SysUserPostConvertor::toModel).toList();
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public SysUserPost insert(SysUserPost sysUserPost) {
        SysUserPostDO sysUserPostDO = SysUserPostConvertor.toDO(sysUserPost);
        sysUserPostMapper.insert(sysUserPostDO);
        return SysUserPostConvertor.toModel(sysUserPostDO);
    }

    @Override
    public void update(SysUserPost sysUserPost) {
        SysUserPostDO sysUserPostDO = SysUserPostConvertor.toDO(sysUserPost);
        int affected = sysUserPostMapper.update(sysUserPostDO);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysUserPostRepository.update id={} 影响行数={}", sysUserPost.getId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void updateByCondition(SysUserPost sysUserPost) {
        int affected = sysUserPostMapper.updateByCondition(SysUserPostConvertor.toDO(sysUserPost));
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysUserPostRepository.updateByCondition id={} 影响行数={}", sysUserPost.getId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void deleteById(Long id) {
        int affected = sysUserPostMapper.deleteById(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "SysUserPostRepository.deleteById id={} 影响行数={}", id, affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.DELETE_FAILED, "删除失败：记录不存在或已被删除");
    }
}
