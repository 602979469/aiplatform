package com.jakt.aiplatform.web.result;

import com.jakt.aiplatform.core.model.enums.ClusterImageStatusEnum;
import com.jakt.aiplatform.core.model.enums.ClusterImageTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 镜像响应 DTO。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ClusterImageResponse extends BaseResult {
    private Long id;
    private String imageName;
    private String version;
    private ClusterImageTypeEnum imageType;
    private String gitUrl;
    private String gitBranch;
    private String dockerfile;
    private String externalImage;
    private String harborRef;
    private String tarName;
    private ClusterImageStatusEnum buildStatus;
    private Integer buildRetryCount;
    private String buildLogPath;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
