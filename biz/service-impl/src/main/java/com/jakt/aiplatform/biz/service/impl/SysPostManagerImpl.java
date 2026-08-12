package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysPostManager;
import com.jakt.aiplatform.core.model.domain.SysPost;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.SysPostQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.service.SysPostService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 岗位管理实现类
 *
 */
@Service
public class SysPostManagerImpl implements SysPostManager {

    /** 岗位领域服务。 */
    private final SysPostService sysPostService;

    public SysPostManagerImpl(SysPostService sysPostService) {
        this.sysPostService = sysPostService;
    }

    @Override
    public SysPost createSysPost(SysPost sysPost) {
        SysPost created = sysPostService.createSysPost(sysPost);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "创建岗位成功 postId={}", created.getPostId());
        return created;
    }

    @Override
    public SysPost getSysPost(Long id) {
        return sysPostService.getSysPost(id);
    }

    @Override
    public PageResult<SysPost> pageSysPosts(SysPostQueryParam query) {
        return sysPostService.findPage(query);
    }

    @Override
    public List<SysPost> listSysPosts(SysPostQueryParam query) {
        return sysPostService.findList(query);
    }

    @Override
    public void updateSysPost(SysPost sysPost) {
        sysPostService.updateSysPost(sysPost);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "更新岗位成功 postId={}", sysPost.getPostId());
    }

    @Override
    public void updateByCondition(SysPost sysPost) {
        sysPostService.updateByCondition(sysPost);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "按条件更新岗位成功 postId={}", sysPost.getPostId());
    }

    @Override
    public void deleteSysPost(Long id) {
        sysPostService.deleteSysPost(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除岗位成功 id={}", id);
    }
}
