package com.jakt.aiplatform.core.model.dto;

import lombok.Data;

/**
 * 文件缩略图：给列表页用的小图字节 + 缓存标识（ETag）。
 */
@Data
public class FileThumbnailView {

    /** 缩略图内容（JPEG）。 */
    private byte[] content;

    /** 缓存标识，内容变化才变（用于 If-None-Match 命中直接回 304）。 */
    private String etag;
}
