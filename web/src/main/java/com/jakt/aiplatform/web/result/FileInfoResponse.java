package com.jakt.aiplatform.web.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件信息响应 DTO（不含文件内容大字段）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileInfoResponse extends BaseResult {

    /** 主键。 */
    private Long id;

    /** 业务命名空间。 */
    private String namespace;

    /** 原始文件名（含扩展名）。 */
    private String originalName;

    /** 文件大小（字节）。 */
    private Long fileSize;

    /** 扩展名（小写，不含点）。 */
    private String fileType;

    /** 备注。 */
    private String remark;

    /** 创建者。 */
    private String createBy;
}
