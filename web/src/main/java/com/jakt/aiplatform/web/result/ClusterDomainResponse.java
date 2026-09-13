package com.jakt.aiplatform.web.result;

import lombok.Data;

/**
 * 域名映射响应：公网 Caddy 入口 + 集群内 Ingress 路由。
 */
@Data
public class ClusterDomainResponse {

    /** 完整域名。 */
    private String domain;

    /** 是否已配置公网 Caddy 站点块。 */
    private Boolean caddy;

    /** 类型：ingress / custom。 */
    private String type;

    /** 公网 Caddy 反代上游。 */
    private String upstream;

    /** Ingress 命名空间。 */
    private String namespace;

    /** 后端 Service 名称。 */
    private String service;

    /** 后端 Service 端口。 */
    private String port;

}
