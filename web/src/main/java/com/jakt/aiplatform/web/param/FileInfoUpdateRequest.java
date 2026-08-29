package com.jakt.aiplatform.web.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 更新文件元信息请求 DTO。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileInfoUpdateRequest extends BaseRequest {

    /** 业务命名空间（定位 + 归属校验）。 */
    private String namespace;

    /** 新的原始文件名（含扩展名）；为空表示不修改。 */
    private String originalName;

    /** 新的备注；为空表示不修改。 */
    private String remark;
}
