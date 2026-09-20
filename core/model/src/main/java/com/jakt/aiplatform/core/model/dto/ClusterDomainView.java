package com.jakt.aiplatform.core.model.dto;

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

    /** 是否站点主域名（jakt.online / www.jakt.online）：置顶且不可删除。 */
    private Boolean primary;

    /** 类型：ingress（集群已有 Ingress 的域名） / custom（仅公网 Caddy 的自定义域名）。 */
    private String type;

    /** 公网 Caddy 反代上游（enable 时写入，如 127.0.0.1:8080）。 */
    private String upstream;

    /** Ingress 所在命名空间。 */
    private String namespace;

    /** 后端 Service 名称。 */
    private String service;

    /** 后端 Service 端口。 */
    private String port;

}
