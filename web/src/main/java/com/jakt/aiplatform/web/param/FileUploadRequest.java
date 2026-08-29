package com.jakt.aiplatform.web.param;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传请求（multipart/form-data：namespace + file + 可选 remark）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileUploadRequest extends BaseRequest {

    /** 业务命名空间。 */
    private String namespace;

    /** 上传文件。 */
    private MultipartFile file;

    /** 备注。 */
    private String remark;
}
