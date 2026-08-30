package com.jakt.aiplatform.common.integration.minio;

import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.common.integration.exception.AiIntegrationErrorCode;
import com.jakt.aiplatform.common.integration.exception.AiIntegrationException;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * MinIO 对象存储客户端：上传/下载/删除/查询统一收口，失败抛集成异常并记 INTEGRATION 日志。
 */
@Component
public class MinioStorage {

    /** MinIO 客户端。 */
    private final MinioClient client;

    /** MinIO 配置。 */
    private final MinioProperties properties;

    public MinioStorage(MinioProperties properties) {
        this.properties = properties;
        this.client = MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
        ensureBucket();
    }

    /**
     * 流式上传对象。
     *
     * @param objectKey   对象键
     * @param stream      内容流（由调用方关闭）
     * @param size        内容字节数
     * @param contentType Content-Type
     */
    public void putObject(String objectKey, InputStream stream, long size, String contentType) {
        try {
            client.putObject(PutObjectArgs.builder()
                    .bucket(properties.getBucket())
                    .object(objectKey)
                    .stream(stream, size, -1)
                    .contentType(contentType)
                    .build());
        } catch (Exception e) {
            throw toIntegrationException("MinIO 上传失败 objectKey={}", e, objectKey);
        }
    }

    /**
     * 流式下载对象（调用方负责关闭返回的流）。
     *
     * @param objectKey 对象键
     * @return 内容流
     */
    public InputStream getObject(String objectKey) {
        try {
            return client.getObject(GetObjectArgs.builder()
                    .bucket(properties.getBucket())
                    .object(objectKey)
                    .build());
        } catch (Exception e) {
            throw toIntegrationException("MinIO 下载失败 objectKey={}", e, objectKey);
        }
    }

    /**
     * 查询对象大小（下载 Content-Length 用）。
     *
     * @param objectKey 对象键
     * @return 字节数
     */
    public long statSize(String objectKey) {
        try {
            StatObjectResponse stat = client.statObject(StatObjectArgs.builder()
                    .bucket(properties.getBucket())
                    .object(objectKey)
                    .build());
            return stat.size();
        } catch (Exception e) {
            throw toIntegrationException("MinIO 查询对象大小失败 objectKey={}", e, objectKey);
        }
    }

    /**
     * 删除对象（对象不存在视为成功，S3 DELETE 幂等）。
     *
     * @param objectKey 对象键
     */
    public void removeObject(String objectKey) {
        try {
            client.removeObject(RemoveObjectArgs.builder()
                    .bucket(properties.getBucket())
                    .object(objectKey)
                    .build());
        } catch (Exception e) {
            throw toIntegrationException("MinIO 删除失败 objectKey={}", e, objectKey);
        }
    }

    /**
     * 确保桶存在（不存在自动创建）。
     */
    private void ensureBucket() {
        try {
            boolean exists = client.bucketExists(BucketExistsArgs.builder()
                    .bucket(properties.getBucket())
                    .build());
            if (!exists) {
                client.makeBucket(MakeBucketArgs.builder().bucket(properties.getBucket()).build());
            }
        } catch (Exception e) {
            throw new IllegalStateException("MinIO 桶初始化失败: " + e.getMessage(), e);
        }
    }

    /**
     * 统一异常封装：INTEGRATION 日志 + 集成异常。
     *
     * @param message 日志模板
     * @param e       原始异常
     * @param args    日志参数
     * @return 集成异常
     */
    private AiIntegrationException toIntegrationException(String message, Exception e, Object... args) {
        LoggerUtil.error(LogFileEnum.INTEGRATION, e, "【MinIO】" + message, args);
        return new AiIntegrationException(AiIntegrationErrorCode.MINIO_ERROR,
                "MinIO 操作失败: " + e.getMessage(), e);
    }
}
