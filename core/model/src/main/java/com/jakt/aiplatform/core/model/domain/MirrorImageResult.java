package com.jakt.aiplatform.core.model.domain;

import lombok.Data;

/**
 * 镜像搜索结果项。
 */
@Data
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

    /** 本地文件是否已存在（/tmp/ruoyi/images）。 */
    private boolean localFileExists;

    /** 本地文件名。 */
    private String localFileName;
}
