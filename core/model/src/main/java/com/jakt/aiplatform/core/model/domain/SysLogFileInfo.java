package com.jakt.aiplatform.core.model.domain;

import lombok.Data;

@Data
public class SysLogFileInfo {
    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件大小（可读格式）
     */
    private String fileSize;

    /**
     * 最后修改时间
     */
    private String lastModified;
}