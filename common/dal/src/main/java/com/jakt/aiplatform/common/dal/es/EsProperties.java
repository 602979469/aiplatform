package com.jakt.aiplatform.common.dal.es;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Elasticsearch 连接配置（题库检索用）。
 */
@Data
@ConfigurationProperties(prefix = "es")
public class EsProperties {

    /** ES 地址。 */
    private String url = "http://elasticsearch.efk.svc.cluster.local:9200";

    /** 用户名。 */
    private String username = "elastic";

    /** 密码。 */
    private String password = "";

    /** 题库索引。 */
    private String questionIndex = "java-kb";
}
