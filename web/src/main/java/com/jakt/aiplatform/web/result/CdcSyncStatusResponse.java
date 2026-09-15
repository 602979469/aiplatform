package com.jakt.aiplatform.web.result;

import lombok.Data;

/**
 * ES 同步运行状态响应。
 */
@Data
public class CdcSyncStatusResponse {

    /** adapter 就绪副本数。 */
    private Integer readyReplicas;

    /** adapter 期望副本数。 */
    private Integer replicas;

    /** canal 实例状态原始 JSON。 */
    private String destinations;
}
