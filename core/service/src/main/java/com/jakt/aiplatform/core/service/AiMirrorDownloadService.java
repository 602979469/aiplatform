package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.domain.MirrorDownloadTask;

import java.io.File;

/**
 * 镜像下载生成领域服务：docker pull + docker save，任务状态内存维护。
 */
public interface AiMirrorDownloadService {

    /**
     * 生成下载（docker pull + docker save 异步执行）。
     *
     * @param repo 仓库路径（如 library/mysql）
     * @param tag  版本号/tag
     * @return 下载任务（本地已有文件时直接返回 ready）
     */
    MirrorDownloadTask generate(String repo, String tag);

    /**
     * 查询任务进度。
     *
     * @param taskId 任务ID
     * @return 下载任务
     */
    MirrorDownloadTask getStatus(String taskId);

    /**
     * 获取本地 tar 文件。
     *
     * @param fileName 文件名
     * @return 文件
     */
    File getFile(String fileName);
}
