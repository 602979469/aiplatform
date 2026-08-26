package com.jakt.aiplatform.common.integration.k8s;

import lombok.Data;

/**
 * Kubernetes 节点基础信息（集成层 DTO）。
 */
@Data
public class K8sNodeInfo {

    /** 节点名称。 */
    private String nodeName;

    /** 角色：master / worker。 */
    private String role;

    /** 架构：amd64 / arm64。 */
    private String arch;

    /** 状态：Ready / NotReady。 */
    private String status;
}
