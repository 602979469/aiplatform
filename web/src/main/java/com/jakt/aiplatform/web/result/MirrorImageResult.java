package com.jakt.aiplatform.web.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 镜像搜索结果项响应 DTO。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MirrorImageResult {

    /** 厂商。 */
    private String vendor;

    /** 仓库路径（如 library/mysql）。 */
    private String repo;

    /** 版本号/tag。 */
    private String tag;

    /** 镜像完整名称（repo:tag）。 */
    private String fullName;

    /** 支持架构（如 amd64,arm64）。 */
    private String arch;

    /** 本地文件是否已存在。 */
    private boolean localFileExists;

    /** 本地文件名。 */
    private String localFileName;
}
