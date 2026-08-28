package com.jakt.aiplatform.core.model.domain;

import lombok.Data;

import java.util.List;

@Data
public class SysLogFileDetail {
    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件大小（可读格式）
     */
    private String fileSize;

    /**
     * 最后修改时间
     */
    private String lastModified;

    /**
     * 总行数
     */
    private Integer totalLines;

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 日志内容
     */
    private List<String> content;
}