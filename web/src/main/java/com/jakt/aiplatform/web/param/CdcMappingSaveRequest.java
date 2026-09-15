package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ES 同步映射保存请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CdcMappingSaveRequest extends BaseRequest {

    /** 任务名（ConfigMap 的 key，如 kb_question）。 */
    @NotBlank(message = "任务名不能为空")
    @Size(max = 64, message = "任务名长度不能超过 64")
    private String name;

    /** 目标 ES 索引（必须已存在）。 */
    @NotBlank(message = "ES 索引不能为空")
    @Size(max = 128, message = "索引名长度不能超过 128")
    private String esIndex;

    /** 映射 SQL（必须带表别名并对每列做限定）。 */
    @NotBlank(message = "SQL 不能为空")
    private String sql;

    /** 主键字段。 */
    @NotBlank(message = "主键字段不能为空")
    @Size(max = 64, message = "主键字段长度不能超过 64")
    private String pk;

    /** 是否 upsert。 */
    private Boolean upsert;

    /** 批量提交条数。 */
    private Integer commitBatch;

    /** 是否保存后滚动重启适配器（null 视为重启）。 */
    private Boolean restart;
}
