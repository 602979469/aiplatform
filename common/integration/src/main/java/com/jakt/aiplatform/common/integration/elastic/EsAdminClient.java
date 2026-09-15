package com.jakt.aiplatform.common.integration.elastic;

import java.util.List;

/**
 * Elasticsearch 管理查询客户端：只读元数据，用于同步配置的索引校验与下拉选择。
 *
 * <p>与检索客户端（题库搜索）分开：这里只做索引级别的存在性判断和列表，不碰文档。
 */
public interface EsAdminClient {

    /**
     * 判断索引是否存在。
     *
     * @param index 索引名
     * @return true 存在
     */
    boolean indexExists(String index);

    /**
     * 列出业务索引（去掉 . 开头的系统索引）。
     *
     * @return 索引名列表（升序）
     */
    List<String> listIndices();
}
