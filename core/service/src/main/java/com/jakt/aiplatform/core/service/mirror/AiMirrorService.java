package com.jakt.aiplatform.core.service.mirror;

import com.jakt.aiplatform.core.model.domain.MirrorDownloadTask;
import com.jakt.aiplatform.core.model.domain.MirrorSearchResponse;

import java.io.File;

/**
 * 镜像加速器领域服务：搜索（官网 + AI 版本匹配 + 本地文件检查）与下载生成（docker pull + docker save）。
 */
public interface AiMirrorService {

    /**
     * 搜索镜像（按下载量取候选，AI 匹配版本，校验当前架构）。
     *
     * @param imageName 镜像名称，可带版本号（如 mysql:8）
     * @param os        客户端操作系统（前端可覆盖，缺省按 UA 解析）
     * @param arch      客户端架构（amd64/arm64）
     * @param userAgent 客户端 User-Agent
     * @return 搜索结果
     */
    MirrorSearchResponse search(String imageName, String os, String arch, String userAgent);

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
