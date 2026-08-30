package com.jakt.aiplatform.common.integration.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MinIO 对象存储配置。
 */
@Data
@ConfigurationProperties(prefix = "ai.minio")
public class MinioProperties {

    /** 服务地址（集群内 http://minio.tsk.svc.cluster.local:9000）。 */
    private String endpoint = "";

    /** 访问密钥。 */
    private String accessKey = "";

    /** 密钥。 */
    private String secretKey = "";

    /** 桶名。 */
    private String bucket = "aiplatform";
}
