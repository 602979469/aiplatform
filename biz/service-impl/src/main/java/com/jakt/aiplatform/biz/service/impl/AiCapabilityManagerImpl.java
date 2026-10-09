package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.AiCapabilityManager;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.core.service.AiCapabilityService;
import org.springframework.stereotype.Service;

/**
 * AI 能力管理实现：委托 core-service。
 */
@Service
public class AiCapabilityManagerImpl implements AiCapabilityManager {

    /** AI 能力领域服务。 */
    private final AiCapabilityService aiCapabilityService;

    public AiCapabilityManagerImpl(AiCapabilityService aiCapabilityService) {
        this.aiCapabilityService = aiCapabilityService;
    }

    @Override
    public String invoke(String sceneCode, String capabilityCode, String input) {
        String reply = aiCapabilityService.invoke(sceneCode, capabilityCode, input);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "AI 能力调用完成 scene={} capability={} 返回长度={}",
                sceneCode, capabilityCode, reply == null ? 0 : reply.length());
        return reply;
    }
}
