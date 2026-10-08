package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.HomeMessageManager;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.core.model.domain.HomeMessage;
import com.jakt.aiplatform.core.service.HomeMessageService;
import org.springframework.stereotype.Service;

/**
 * 首页留言板管理实现。
 */
@Service
public class HomeMessageManagerImpl implements HomeMessageManager {

    /** 首页留言板领域服务。 */
    private final HomeMessageService homeMessageService;

    public HomeMessageManagerImpl(HomeMessageService homeMessageService) {
        this.homeMessageService = homeMessageService;
    }

    @Override
    public HomeMessage postMessage(String content, String clientIp, String createBy) {
        HomeMessage created = homeMessageService.postMessage(content, clientIp, createBy);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "留言板新增留言 id={} identity={} color={}",
                created.getId(), created.getAvatarName(), created.getColorIndex());
        return created;
    }

    @Override
    public PageResult<HomeMessage> pageMessages(int pageNum, int pageSize) {
        return homeMessageService.findPage(pageNum, pageSize);
    }
}
