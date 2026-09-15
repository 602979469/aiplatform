package com.jakt.aiplatform.core.model.domain;

import lombok.Data;

/**
 * ES 同步映射：一条对应 canal-adapter 映射 ConfigMap 里的一个 yml 任务。
 */
@Data
public class CdcEsMapping {

    /** 任务名（ConfigMap 的 key，落成 xxx.yml，全量导入接口也用它）。 */
    private String name;

    /** 目标 ES 索引：必须已存在，canal 不会自动创建。 */
    private String esIndex;

    /** 映射 SQL：必须带表别名并对每列做限定。 */
    private String sql;

    /** 主键字段：决定 ES 文档 _id。 */
    private String pk;

    /** 是否 upsert：ES 中不存在时插入。 */
    private Boolean upsert;

    /** 批量提交条数。 */
    private Integer commitBatch;

    /** canal destination。 */
    private String destination;

    /** canal groupId。 */
    private String groupId;

    /** 数据源 key。 */
    private String dataSourceKey;

    /** 原始 yml 文本（编辑/展示用，保存时可原样保留）。 */
    private String rawYml;

    /** SQL 中解析出的表名。 */
    private String tableName;
}
