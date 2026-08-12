package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysUserPostManager;
import com.jakt.aiplatform.core.model.domain.SysUserPost;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysUserPostQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.service.SysUserPostService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户岗位关联管理实现类
 *
 */
@Service
public class SysUserPostManagerImpl implements SysUserPostManager {

    /** 用户岗位关联领域服务。 */
    private final SysUserPostService sysUserPostService;

    public SysUserPostManagerImpl(SysUserPostService sysUserPostService) {
        this.sysUserPostService = sysUserPostService;
    }

    @Override
    public SysUserPost createSysUserPost(SysUserPost sysUserPost) {
        SysUserPost created = sysUserPostService.createSysUserPost(sysUserPost);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "创建用户岗位关联成功 id={}", created.getId());
        return created;
    }

    @Override
    public SysUserPost getSysUserPost(Long id) {
        return sysUserPostService.getSysUserPost(id);
    }

    @Override
    public PageResult<SysUserPost> pageSysUserPosts(SysUserPostQueryParam query) {
        return sysUserPostService.findPage(query);
    }

    @Override
    public List<SysUserPost> listSysUserPosts(SysUserPostQueryParam query) {
        return sysUserPostService.findList(query);
    }

    @Override
    public void updateSysUserPost(SysUserPost sysUserPost) {
        sysUserPostService.updateSysUserPost(sysUserPost);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "更新用户岗位关联成功 id={}", sysUserPost.getId());
    }

    @Override
    public void updateByCondition(SysUserPost sysUserPost) {
        sysUserPostService.updateByCondition(sysUserPost);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "按条件更新用户岗位关联成功 id={}", sysUserPost.getId());
    }

    @Override
    public void deleteSysUserPost(Long id) {
        sysUserPostService.deleteSysUserPost(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除用户岗位关联成功 id={}", id);
    }
}
