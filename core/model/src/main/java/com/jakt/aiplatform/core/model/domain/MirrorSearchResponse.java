package com.jakt.aiplatform.core.model.domain;

import lombok.Data;

import java.util.List;

/**
 * 镜像搜索结果。
 */
@Data
public class MirrorSearchResponse {

    /** 客户端操作系统。 */
    private String os;

    /** 客户端架构。 */
    private String arch;

    /** 搜索结果。 */
    private List<MirrorImageResult> results;
}
