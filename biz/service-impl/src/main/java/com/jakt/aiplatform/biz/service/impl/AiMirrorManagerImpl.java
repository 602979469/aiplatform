package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.AiMirrorManager;
import com.jakt.aiplatform.core.model.domain.MirrorDownloadTask;
import com.jakt.aiplatform.core.model.domain.MirrorSearchResponse;
import com.jakt.aiplatform.common.util.enums.LogFileEnum;
import com.jakt.aiplatform.common.util.tools.LoggerUtil;
import com.jakt.aiplatform.core.service.mirror.AiMirrorService;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * 镜像加速器管理实现：用例编排，只依赖 core-service 与 core-model。
 */
@Service
public class AiMirrorManagerImpl implements AiMirrorManager {

    /** 镜像加速器领域服务（搜索 + 下载生成）。 */
    private final AiMirrorService aiMirrorService;

    public AiMirrorManagerImpl(AiMirrorService aiMirrorService) {
        this.aiMirrorService = aiMirrorService;
    }

    @Override
    public MirrorSearchResponse search(String imageName, String os, String arch, String userAgent) {
        return aiMirrorService.search(imageName, os, arch, userAgent);
    }

    @Override
    public MirrorDownloadTask generate(String repo, String tag) {
        MirrorDownloadTask task = aiMirrorService.generate(repo, tag);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE,
                "【镜像加速器】生成下载任务 taskId={} repo={}:{}", task.getTaskId(), repo, tag);
        return task;
    }

    @Override
    public MirrorDownloadTask getStatus(String taskId) {
        return aiMirrorService.getStatus(taskId);
    }

    @Override
    public File getFile(String fileName) {
        return aiMirrorService.getFile(fileName);
    }
}
