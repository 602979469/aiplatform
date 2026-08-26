package com.jakt.aiplatform.web.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * 集群节点信息响应。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClusterNodeResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 节点名称。 */
    private String nodeName;

    /** 角色：master / worker。 */
    private String role;

    /** 架构：AMD / ARM。 */
    private String arch;

    /** 状态：Ready / NotReady。 */
    private String status;

    /** 该节点上系统管理业务 pod 数量，按命名空间分组。 */
    private Map<String, Integer> podCountByNamespace;
}
