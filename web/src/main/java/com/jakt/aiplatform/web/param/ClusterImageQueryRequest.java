package com.jakt.aiplatform.web.param;

import com.jakt.aiplatform.core.model.enums.ClusterImageStatusEnum;
import com.jakt.aiplatform.core.model.enums.ClusterImageTypeEnum;
import com.jakt.aiplatform.web.param.PageQueryRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 镜像查询请求 DTO。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClusterImageQueryRequest extends PageQueryRequest {
    private String imageName;

    private String version;

    private ClusterImageTypeEnum imageType;

    private ClusterImageStatusEnum buildStatus;
}
