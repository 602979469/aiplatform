package com.jakt.aiplatform.common.integration.k8s;

import lombok.Data;

/**
 * Pod 基础信息（集成层 DTO，大盘按节点统计用）。
 */
@Data
public class K8sPodInfo {

    /** Pod 所在命名空间。 */
    private String namespace;

    /** Pod 名称。 */
    private String podName;

    /** Pod 所在节点；未调度时为空。 */
    private String nodeName;
}
