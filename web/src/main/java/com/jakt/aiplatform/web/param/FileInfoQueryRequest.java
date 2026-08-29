package com.jakt.aiplatform.web.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件列表分页查询请求：namespace 必填，fileName 可选模糊匹配。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileInfoQueryRequest extends PageQueryRequest {

    /** 业务命名空间（必填）。 */
    private String namespace;

    /** 原始文件名（模糊匹配，可选）。 */
    private String fileName;
}
