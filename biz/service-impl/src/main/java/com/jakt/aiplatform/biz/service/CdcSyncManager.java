package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.core.model.domain.CdcEsMapping;
import com.jakt.aiplatform.core.model.domain.CdcMappingPreview;
import com.jakt.aiplatform.core.model.domain.CdcSyncStatus;

import java.util.List;

/**
 * ES 同步（canal CDC）管理：映射配置的增删改查 + 全量导入触发 + 运行状态。
 */
public interface CdcSyncManager {

    /**
     * 列出全部同步映射。
     *
     * @return 映射列表
     */
    List<CdcEsMapping> listMappings();

    /**
     * 查询单个映射。
     *
     * @param name 任务名（可带 .yml）
     * @return 映射；不存在返回 null
     */
    CdcEsMapping getMapping(String name);

    /**
     * 预检配置并生成 yml 预览。
     *
     * @param mapping 映射
     * @return 校验项 + yml 预览
     */
    CdcMappingPreview preview(CdcEsMapping mapping);

    /**
     * 保存映射（新增或覆盖）。
     *
     * @param mapping 映射
     * @param restart 是否顺带滚动重启适配器（null 视为重启）
     */
    void saveMapping(CdcEsMapping mapping, Boolean restart);

    /**
     * 删除映射。
     *
     * @param name    任务名
     * @param restart 是否顺带滚动重启适配器（null 视为重启）
     */
    void deleteMapping(String name, Boolean restart);

    /**
     * SQL 格式化（补表别名、限定列名、按列换行）。
     *
     * @param sql 原始 SQL
     * @return 格式化后的 SQL
     */
    String formatSql(String sql);

    /**
     * 触发全量导入。
     *
     * @param name 任务名
     * @return canal-adapter 原始响应
     */
    String triggerEtl(String name);

    /**
     * 滚动重启 canal-adapter。
     */
    void restartAdapter();

    /**
     * 查询同步运行状态。
     *
     * @return 运行状态
     */
    CdcSyncStatus status();

    /**
     * 列出 ES 业务索引。
     *
     * @return 索引名列表
     */
    List<String> listEsIndices();
}
