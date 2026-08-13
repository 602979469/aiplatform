package com.jakt.aiplatform.web.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 镜像搜索结果响应 DTO。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MirrorSearchResponse {

    /** 客户端操作系统。 */
    private String os;

    /** 客户端架构。 */
    private String arch;

    /** 搜索结果。 */
    private List<MirrorImageResult> results;
}
