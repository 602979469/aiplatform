package com.jakt.aiplatform.biz.service;

import lombok.Data;

/**
 * 域名映射视图：公网 Caddy 入口 + 集群内 Ingress 路由的合并结果。
 */
@Data
public class ClusterDomainView {

    /** 完整域名（xxxx.jakt.online）。 */
    private String domain;

    /** 是否已配置公网 Caddy 站点块。 */
    private Boolean caddy;

    /** 是否已有集群内 Ingress 规则。 */
    private Boolean ingress;

    /** Ingress 名称。 */
    private String ingressName;

    /** Ingress 所在命名空间。 */
    private String namespace;

    /** 后端 Service 名称。 */
    private String service;

    /** 后端 Service 端口。 */
    private String port;

    /** 是否为「域名映射」功能纳管（Ingress 名 dm- 前缀）。 */
    private Boolean managed;
}
