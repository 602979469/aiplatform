package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.core.model.domain.MirrorDownloadTask;
import com.jakt.aiplatform.core.model.domain.MirrorSearchResponse;

import java.io.File;

/**
 * 镜像加速器管理接口：搜索/下载用例编排。
 */
public interface AiMirrorManager {

    /**
     * 搜索镜像。
     *
     * @param imageName 镜像名称，可带版本号
     * @param os        客户端操作系统
     * @param arch      客户端架构
     * @param userAgent 客户端 User-Agent
     * @return 搜索结果
     */
    MirrorSearchResponse search(String imageName, String os, String arch, String userAgent);

    /**
     * 生成下载（docker pull + docker save）。
     *
     * @param repo 仓库路径
     * @param tag  版本号/tag
     * @return 下载任务
     */
    MirrorDownloadTask generate(String repo, String tag);

    /**
     * 查询下载进度。
     *
     * @param taskId 任务ID
     * @return 下载任务
     */
    MirrorDownloadTask getStatus(String taskId);

}
