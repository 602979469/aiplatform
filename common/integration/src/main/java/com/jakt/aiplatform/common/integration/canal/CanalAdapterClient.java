package com.jakt.aiplatform.common.integration.canal;

/**
 * canal-adapter 管理接口客户端。
 *
 * <p>canal-adapter 没有页面，只有少量 HTTP 接口：{@code GET /destinations} 查同步实例状态，
 * {@code POST /etl/{adapter}/{task}} 触发某个映射任务的全量导入（把 MySQL 存量数据补进 ES）。
 * 增量同步由 canal-server 读 binlog 自动驱动，不在本客户端职责内。
 *
 * <p>所有方法失败抛 {@link com.jakt.aiplatform.common.integration.exception.AiIntegrationException}，
 * 集成层内部已按 {@link com.jakt.aiplatform.common.framework.enums.LogFileEnum#INTEGRATION} 记录日志。
 */
public interface CanalAdapterClient {

    /**
     * 查询同步实例状态。
     *
     * @return canal-adapter 原始响应（JSON 数组，形如 [{"destination":"example","status":"on"}]）
     */
    String destinations();

    /**
     * 触发指定映射任务的全量导入。
     *
     * @param adapter 适配器名（ES 用 es8）
     * @param task    任务名（映射 yml 文件名，如 cluster_pod_config.yml）
     * @return canal-adapter 原始响应（JSON，形如 {"succeeded":true,"resultMessage":"导入ES 数据：2 条"}）
     */
    String triggerEtl(String adapter, String task);
}
