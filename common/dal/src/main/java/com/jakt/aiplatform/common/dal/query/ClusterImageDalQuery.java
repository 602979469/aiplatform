package com.jakt.aiplatform.common.dal.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 镜像表查询条件。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClusterImageDalQuery extends DalPageQuery {
    /** 主键。 */
    private Long id;

    /** 镜像名（模糊）。 */
    private String imageName;

    /** 版本（模糊）。 */
    private String version;

    /** 来源类型。 */
    private String imageType;

    /** 构建状态。 */
    private String buildStatus;
}
