package com.jakt.aiplatform.core.model.template;

import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.exception.AiPlatformException;
import com.jakt.aiplatform.core.model.exception.AiPlatformExceptionResolver;
import com.jakt.aiplatform.core.model.result.Result;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;

/**
 * 事务业务模板：在事务中执行业务回调，统一捕获异常并组装返回结果。
 *
 * <p>约定：
 * <ul>
 *     <li>正常返回 → success=true + data</li>
 *     <li>业务异常（{@link AiPlatformException}）→ success=false + errorCode + message，不记录日志（业务需要时自行记录）</li>
 *     <li>业务异常 / 外部集成异常（可被 {@link AiPlatformExceptionResolver} 解析）→ 转换对应业务错误码，不记录日志</li>
 *     <li>未知异常 → success=false + SYSTEM_ERROR，并记录 error 日志</li>
 * </ul>
 *
 * <p>本类位于 core-model，不引入 Spring 依赖：事务执行能力通过 {@link TransactionExecutor} 抽象，
 * Spring {@code TransactionTemplate} 的包装实现由 common-dal 装配为 Bean。
 */
public class AiPlatformTransactionTemplate {

    /** 事务执行器。 */
    private final TransactionExecutor transactionExecutor;

    public AiPlatformTransactionTemplate(TransactionExecutor transactionExecutor) {
        this.transactionExecutor = transactionExecutor;
    }

    /**
     * 执行有返回值的事务用例。
     *
     * @param callback 业务回调
     * @param <T>      返回类型
     * @return 统一返回体（成功 data / 失败 errorCodeEnum + errorMessage）
     */
    public <T> Result<T> execute(Callback<T> callback) {
        try {
            return Result.ok(transactionExecutor.execute(callback::execute));
        } catch (AiPlatformException e) {
            // 业务异常：已知原因，不记录日志（业务需要时自行记录）
            return Result.fail(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            // 尝试解析：业务异常 / 外部集成异常可映射为业务错误码；解析失败按未知异常处理
            AiPlatformException resolved = AiPlatformExceptionResolver.resolve(e);
            if (resolved != null) {
                return Result.fail(resolved.getErrorCode(), resolved.getMessage());
            }
            // 未知异常：必须记录日志，统一 SYSTEM_ERROR
            AiPlatformLoggerUtil.error(LogFileEnum.COMMON_ERROR, "事务执行失败", e);
            return Result.fail(ErrorCodeEnum.SYSTEM_ERROR);
        }
    }

    /**
     * 执行无返回值的事务用例。
     *
     * @param callback 无返回值业务回调
     * @return 统一返回体（Result&lt;Void&gt;）
     */
    public Result<Void> executeWithoutResult(CallbackWithoutResult callback) {
        return execute(() -> {
            callback.execute();
            return null;
        });
    }

    /**
     * 事务执行器：由数据访问层用 Spring TransactionTemplate 实现。
     */
    @FunctionalInterface
    public interface TransactionExecutor {

        /**
         * 在事务中执行动作并返回结果。
         *
         * @param action 事务内动作
         * @param <T>    返回类型
         * @return 动作结果
         */
        <T> T execute(TransactionAction<T> action);
    }

    /**
     * 事务内动作。
     */
    @FunctionalInterface
    public interface TransactionAction<T> {

        /** 执行业务逻辑。 */
        T run();
    }

    /**
     * 业务回调。
     */
    @FunctionalInterface
    public interface Callback<T> {

        /** 执行业务逻辑。 */
        T execute();
    }

    /**
     * 无返回值业务回调。
     */
    @FunctionalInterface
    public interface CallbackWithoutResult {

        /** 执行业务逻辑。 */
        void execute();
    }
}
