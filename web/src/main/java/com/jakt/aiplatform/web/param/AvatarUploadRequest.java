package com.jakt.aiplatform.web.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 头像上传请求（JSON 变体）：avatarfile 传 base64 字符串，Jackson 自动解码为字节数组。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AvatarUploadRequest extends BaseRequest {

    /** 头像文件字节（JSON 中为 base64 字符串）。 */
    private byte[] avatarfile;

    /** 原始文件名（含扩展名）。 */
    private String filename;
}
