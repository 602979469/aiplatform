package com.jakt.aiplatform.core.service;

/**
 * cluster-ci 脚本同步服务：调度前把 Java 资源内的脚本同步到 master bin 目录。
 */
public interface ClusterScriptSyncService {

    /**
     * 同步远端脚本：缺失或 hash 不一致时上传覆盖，新增脚本文件自动写入。
     */
    void syncScripts();
}
