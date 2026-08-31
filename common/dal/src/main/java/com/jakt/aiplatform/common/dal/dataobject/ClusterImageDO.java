package com.jakt.aiplatform.common.dal.dataobject;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 镜像表 DO 对象。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClusterImageDO extends BaseDO {
    /** 主键。 */
    private Long id;

    /** 标准化镜像名。 */
    private String imageName;

    /** 版本/tag。 */
    private String version;

    /** 来源类型（BUILD/EXTERNAL）。 */
    private String imageType;

    /** git 地址。 */
    private String gitUrl;

    /** git 分支。 */
    private String gitBranch;

    /** Dockerfile 内容。 */
    private String dockerfile;

    /** 外部镜像地址。 */
    private String externalImage;

    /** Harbor 完整引用。 */
    private String harborRef;

    /** tar 归档名。 */
    private String tarName;

    /** 构建状态。 */
    private String buildStatus;

    /** 构建失败已重试次数。 */
    private Integer buildRetryCount;

    /** 构建日志路径。 */
    private String buildLogPath;

    /** 创建者。 */
    private String createBy;

    /** 更新者。 */
    private String updateBy;

    /** 备注。 */
    private String remark;
}
