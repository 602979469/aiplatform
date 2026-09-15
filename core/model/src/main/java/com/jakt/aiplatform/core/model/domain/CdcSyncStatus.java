package com.jakt.aiplatform.core.model.domain;

import lombok.Data;

/**
 * 同步运行状态：canal-adapter 滚动状态 + canal 实例状态。
 */
@Data
public class CdcSyncStatus {

    /** adapter 就绪副本数。 */
    private Integer readyReplicas;

    /** adapter 期望副本数。 */
    private Integer replicas;

    /** canal-adapter 返回的实例状态原始 JSON。 */
    private String destinations;
}
