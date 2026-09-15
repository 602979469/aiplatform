package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.domain.CdcEsMapping;
import com.jakt.aiplatform.core.model.domain.CdcMappingPreview;
import com.jakt.aiplatform.core.model.domain.CdcSyncStatus;

import java.util.List;

/**
 * ES 同步（canal CDC）配置用例：映射 ConfigMap 的读写 + 任务触发 + 运行状态。
 *
 * <p>canal-adapter 的配置是挂进 Pod 的文件，没有热加载接口：改完必须滚动重启适配器才会生效，
 * 存量数据还要手动触发一次全量导入（增量变更由 canal-server 读 binlog 自动驱动）。
 */
public interface CdcSyncService {

    /**
     * 列出全部同步映射。
     *
     * @return 映射列表（按任务名升序）
     */
    List<CdcEsMapping> listMappings();

    /**
     * 查询单个映射。
     *
     * @param name 任务名（可带或不带 .yml 后缀）
     * @return 映射；不存在返回 null
     */
    CdcEsMapping getMapping(String name);

    /**
     * 预检：校验配置并生成将要写入的 yml 预览（前端"创建指引"数据源）。
     *
     * @param mapping 待保存的映射
     * @return 校验项列表 + yml 预览
     */
    CdcMappingPreview preview(CdcEsMapping mapping);

    /**
     * 保存映射（新增或覆盖同名任务）。
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
     * SQL 格式化：补表别名、限定列名、按列换行（顺带修掉 canal 增量 UPDATE 的空指针坑）。
     *
     * @param sql 原始 SQL
     * @return 格式化后的 SQL
     */
    String formatSql(String sql);

    /**
     * 触发一次全量导入（把 MySQL 存量数据补进 ES）。
     *
     * @param name 任务名
     * @return canal-adapter 原始响应
     */
    String triggerEtl(String name);

    /**
     * 滚动重启 canal-adapter（让配置生效）。
     */
    void restartAdapter();

    /**
     * 查询同步运行状态。
     *
     * @return 适配器副本状态 + canal 实例状态
     */
    CdcSyncStatus status();

    /**
     * 列出 ES 业务索引（创建映射时的选择项）。
     *
     * @return 索引名列表
     */
    List<String> listEsIndices();
}
