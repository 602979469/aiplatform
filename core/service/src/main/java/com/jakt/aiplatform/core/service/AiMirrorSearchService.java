package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.domain.MirrorSearchResponse;

/**
 * 镜像搜索领域服务：官网搜索 + AI 版本匹配 + 本地文件检查。
 */
public interface AiMirrorSearchService {

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
}
