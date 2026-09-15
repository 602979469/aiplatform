package com.jakt.aiplatform.web.result;

import lombok.Data;

/**
 * ES 同步映射响应。
 */
@Data
public class CdcMappingResponse {

    /** 任务名。 */
    private String name;

    /** 目标 ES 索引。 */
    private String esIndex;

    /** 映射 SQL。 */
    private String sql;

    /** 主键字段。 */
    private String pk;

    /** 是否 upsert。 */
    private Boolean upsert;

    /** 批量提交条数。 */
    private Integer commitBatch;

    /** 来源表名。 */
    private String tableName;

    /** 原始 yml（详情展示/编辑用）。 */
    private String rawYml;
}
