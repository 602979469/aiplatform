package com.jakt.aiplatform.common.integration.exception;

/**
 * 外部集成错误码（common-integration 内部定义）。
 *
 * <p>取值与 core-model {@code ErrorCodeEnum.name()} 对齐时，业务模板可将对应异常解析为业务异常；
 * 未对齐的取值（如 UNKNOWN）按未知异常处理。
 */
public final class AiIntegrationErrorCode {

    /** DeepSeek 接口调用失败。 */
    public static final String DEEPSEEK_API_ERROR = "DEEPSEEK_API_ERROR";

    /** 镜像加速器接口调用失败。 */
    public static final String XUANYUAN_API_ERROR = "XUANYUAN_API_ERROR";

    /** 外部服务认证失败。 */
    public static final String AUTH_ERROR = "AUTH_ERROR";

    /** 外部服务调用超时。 */
    public static final String TIMEOUT = "TIMEOUT";

    /** 未知外部错误。 */
    public static final String UNKNOWN = "UNKNOWN";

    private AiIntegrationErrorCode() {
    }
}
