package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.AiMirrorManager;
import com.jakt.aiplatform.core.model.domain.MirrorDownloadTask;
import com.jakt.aiplatform.core.model.domain.MirrorSearchResponse;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.service.AiMirrorDownloadService;
import com.jakt.aiplatform.core.service.AiMirrorSearchService;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * 镜像加速器管理实现：用例编排，只依赖 core-service 与 core-model。
 */
@Service
public class AiMirrorManagerImpl implements AiMirrorManager {

    private final AiMirrorSearchService aiMirrorSearchService;

    private final AiMirrorDownloadService aiMirrorDownloadService;

    public AiMirrorManagerImpl(AiMirrorSearchService aiMirrorSearchService,
                               AiMirrorDownloadService aiMirrorDownloadService) {
        this.aiMirrorSearchService = aiMirrorSearchService;
        this.aiMirrorDownloadService = aiMirrorDownloadService;
    }

    @Override
    public MirrorSearchResponse search(String imageName, String os, String arch, String userAgent) {
        return aiMirrorSearchService.search(imageName, os, arch, userAgent);
    }

    @Override
    public MirrorDownloadTask generate(String repo, String tag) {
        MirrorDownloadTask task = aiMirrorDownloadService.generate(repo, tag);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE,
                "【镜像加速器】生成下载任务 taskId={} repo={}:{}", task.getTaskId(), repo, tag);
        return task;
    }

    @Override
    public MirrorDownloadTask getStatus(String taskId) {
        return aiMirrorDownloadService.getStatus(taskId);
    }

    @Override
    public File getFile(String fileName) {
        return aiMirrorDownloadService.getFile(fileName);
    }
}
