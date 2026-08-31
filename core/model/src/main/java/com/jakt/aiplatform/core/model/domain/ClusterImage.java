package com.jakt.aiplatform.core.model.domain;

import com.jakt.aiplatform.common.framework.model.BaseModel;
import com.jakt.aiplatform.core.model.enums.ClusterImageStatusEnum;
import com.jakt.aiplatform.core.model.enums.ClusterImageTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 镜像表领域模型（一个镜像名多个版本）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClusterImage extends BaseModel {
    /** 主键。 */
    private Long id;

    /** 标准化镜像名（小写字母/数字/下划线）。 */
    private String imageName;

    /** 版本/tag。 */
    private String version;

    /** 来源类型（BUILD 自研 / EXTERNAL 现成）。 */
    private ClusterImageTypeEnum imageType;

    /** git 地址（imageType=BUILD）。 */
    private String gitUrl;

    /** git 分支（imageType=BUILD）。 */
    private String gitBranch;

    /** Dockerfile 内容（imageType=BUILD，必填）。 */
    private String dockerfile;

    /** 外部镜像地址（imageType=EXTERNAL）。 */
    private String externalImage;

    /** Harbor 完整引用，如 harbor.jakt.online/library/xxx:tag。 */
    private String harborRef;

    /** tar 归档名，如 xxx_tag.tar.gz（MinIO image-tars/）。 */
    private String tarName;

    /** 构建状态。 */
    private ClusterImageStatusEnum buildStatus;

    /** 构建失败已重试次数（≤3）。 */
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
