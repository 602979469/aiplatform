package com.jakt.aiplatform.core.model.exception;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.EnumUtil;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;

/**
 * 异常解析器：解包异常根因，尝试将异常转换为业务异常。
 *
 * <p>规则：
 * <ul>
 *     <li>根因是 {@link AiPlatformException} → 直接返回该业务异常</li>
 *     <li>根因实现 {@link ErrorCodeCarrier}（如 common-integration 的外部集成异常）且错误码与
 *         {@link ErrorCodeEnum} 同名 → 转换为对应业务异常</li>
 *     <li>其余情况 → 返回 null（调用方按未知异常处理）</li>
 * </ul>
 */
public final class AiPlatformExceptionResolver {

    private AiPlatformExceptionResolver() {
    }

    /**
     * 解析异常，可转换时返回业务异常，否则返回 null。
     *
     * @param throwable 原始异常
     * @return 业务异常；无法转换返回 null
     */
    public static AiPlatformException resolve(Throwable throwable) {
        Throwable root = ExceptionUtil.getRootCause(throwable);
        if (root instanceof AiPlatformException bizException) {
            return bizException;
        }
        if (root instanceof ErrorCodeCarrier carrier) {
            ErrorCodeEnum mapped = EnumUtil.getBy(ErrorCodeEnum.class,
                    errorCodeEnum -> errorCodeEnum.name().equals(carrier.getErrorCode()));
            if (mapped != null) {
                return new AiPlatformException(mapped, root.getMessage());
            }
        }
        return null;
    }
}
