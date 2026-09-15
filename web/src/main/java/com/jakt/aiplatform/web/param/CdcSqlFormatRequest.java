package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * SQL 格式化请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CdcSqlFormatRequest extends BaseRequest {

    /** 原始 SQL。 */
    @NotBlank(message = "SQL 不能为空")
    private String sql;
}
