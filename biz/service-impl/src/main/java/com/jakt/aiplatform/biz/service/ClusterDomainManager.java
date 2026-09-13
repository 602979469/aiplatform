package com.jakt.aiplatform.biz.service;

import java.util.List;

/**
 * 域名映射管理：公网 Caddy（公网入口）+ 集群 Ingress（内部路由）联动。
 */
public interface ClusterDomainManager {

    /**
     * 列出全部域名：集群 Ingress 域名 ∪ 公网 Caddy 站点。
     *
     * @return 域名列表
     */
    List<ClusterDomainView> list();

    /**
     * 开启公网映射（新增 Caddy 站点块 + reload）；已存在则保持不变。
     *
     * @param domain   完整域名（xxxx.jakt.online）
     * @param upstream 反代上游（可空，默认 127.0.0.1:8080）
     */
    void enable(String domain, String upstream);

    /**
     * 关闭公网映射（删除 Caddy 站点块 + reload），不影响集群 Ingress。
     *
     * @param domain 完整域名
     */
    void disable(String domain);
}
