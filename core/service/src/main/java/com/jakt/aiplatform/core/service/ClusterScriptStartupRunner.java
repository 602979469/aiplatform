package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 启动时同步 cluster-ci 脚本：提前暴露 SSH 连通/脚本缺失或变更问题，失败阻塞启动。
 * 部署业务 pod 时不再检查，便于临时在服务器上改脚本调试（Java 不感知、不覆盖）。
 */
@Component
public class ClusterScriptStartupRunner implements ApplicationRunner {

    /** cluster-ci 脚本同步服务。 */
    private final ClusterScriptSyncService clusterScriptSyncService;

    /** cluster-ci 配置（启动同步开关）。 */
    private final ClusterCiProperties ciProperties;

    public ClusterScriptStartupRunner(ClusterScriptSyncService clusterScriptSyncService,
                                      ClusterCiProperties ciProperties) {
        this.clusterScriptSyncService = clusterScriptSyncService;
        this.ciProperties = ciProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!ciProperties.isSyncOnStartup()) {
            LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "启动脚本同步已关闭（cluster.ci.sync-on-startup=false）");
            return;
        }
        try {
            clusterScriptSyncService.syncScripts();
            LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "启动脚本同步完成");
        } catch (Exception e) {
            LoggerUtil.error(LogFileEnum.COMMON_ERROR, e,
                    "启动脚本同步失败，阻止应用启动（SSH 不可达或脚本资源异常，请检查环境）");
            throw e;
        }
    }
}
