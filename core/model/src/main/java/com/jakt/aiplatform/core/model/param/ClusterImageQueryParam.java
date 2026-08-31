package com.jakt.aiplatform.core.model.param;

import com.jakt.aiplatform.common.framework.param.PageParam;
import com.jakt.aiplatform.core.model.enums.ClusterImageStatusEnum;
import com.jakt.aiplatform.core.model.enums.ClusterImageTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 镜像查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClusterImageQueryParam extends PageParam {
    /** 镜像名（模糊）。 */
    private String imageName;

    /** 版本（模糊）。 */
    private String version;

    /** 来源类型。 */
    private ClusterImageTypeEnum imageType;

    /** 构建状态。 */
    private ClusterImageStatusEnum buildStatus;
}
