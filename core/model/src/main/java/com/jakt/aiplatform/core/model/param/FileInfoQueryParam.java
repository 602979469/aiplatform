package com.jakt.aiplatform.core.model.param;

import com.jakt.aiplatform.common.framework.param.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件信息表查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileInfoQueryParam extends PageParam {

    /** 业务命名空间（必填，隔离维度）。 */
    private String namespace;

    /** 原始文件名（模糊匹配，可选）。 */
    private String originalName;
}
