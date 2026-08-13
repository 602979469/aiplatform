package com.jakt.aiplatform.core.model.exception;

/**
 * 携带错误码的异常标记：业务模板捕获异常时尝试解析错误码并转换为业务异常。
 *
 * <p>外部集成异常（common-integration）实现本接口，错误码与 {@code ErrorCodeEnum.name()} 同名时
 * 可被 {@code AiPlatformTransactionTemplate} 自动映射为业务错误。
 */
public interface ErrorCodeCarrier {

    /** 错误码（与 ErrorCodeEnum.name() 对齐时可被模板解析）。 */
    String getErrorCode();
}
