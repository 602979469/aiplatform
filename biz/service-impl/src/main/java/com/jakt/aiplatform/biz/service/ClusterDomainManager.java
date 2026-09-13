package com.jakt.aiplatform.biz.service;

import java.util.List;

/**
 * 域名映射管理：公网 Caddy（公网入口）+ 集群 Ingress（内部路由）联动。
 */
public interface ClusterDomainManager {

    /**
     * 列出全部域名映射（Caddy 站点 ∪ Ingress 域名）。
     *
     * @return 域名映射列表
     */
    List<ClusterDomainView> list();

    /**
     * 新增映射：建/更新 Ingress（命名空间+Service 非空时）+ Caddy 站点块 + reload。
     *
     * @param domain    完整域名（xxxx.jakt.online）
     * @param namespace 目标命名空间（可空）
     * @param service   目标 Service（可空）
     * @param port      目标端口（可空，默认 80）
     */
    void add(String domain, String namespace, String service, Integer port);

    /**
     * 删除映射：删 Ingress（dm- 前缀）+ Caddy 站点块 + reload。
     *
     * @param domain 完整域名
     */
    void remove(String domain);
}
