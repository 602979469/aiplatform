package com.jakt.aiplatform.web.template;

import com.jakt.aiplatform.web.result.ApiResult;
import com.jakt.aiplatform.common.util.error.CommonErrorCode;
import com.jakt.aiplatform.common.framework.error.CommonException;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import jakarta.validation.ValidationException;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * web 层业务模板：统一请求摘要、参数校验、异常封装与 Result 包装，Controller 只提供业务回调。
 *
 * <p>执行流程：beforeService（参数校验）→ execute（业务）→ afterService（finally）→ 请求摘要（common-digest）。
 * 每个请求只写一条 common-digest 摘要（入参/返回值）；真正的系统异常写 common-error；
 * 业务失败（校验失败/业务异常/数据约束）不再单独打日志，由摘要里的返回值体现，避免重复污染 biz-service。
 */
public final class ApiTemplate {

    /** 日志时间格式。 */
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private ApiTemplate() {
    }

    /**
     * 执行 web 用例：beforeService → execute → afterService（finally），统一包装返回结果。
     *
     * @param param    入参（DTO 或路径参数）
     * @param callback 业务回调
     * @param <P>      入参类型
     * @param <R>      出参类型
     * @return 统一返回体
     */
    public static <P, R> ApiResult<R> execute(P param, Callback<P, R> callback) {
        String caller = resolveCaller();
        String startTime = LocalDateTime.now().format(TIME_FORMATTER);
        ApiResult<R> result = null;
        R data = null;

        try {
            try {
                callback.beforeService(param);
            } catch (CommonException | ValidationException e) {
                result = ApiResult.fail(CommonErrorCode.PARAM_INVALID, e.getMessage());
            } catch (Exception e) {
                LoggerUtil.error(LogFileEnum.COMMON_ERROR, e, "执行{}校验逻辑时抛出异常", caller);
                result = ApiResult.fail(CommonErrorCode.SYSTEM_ERROR);
            }

            if (result == null) {
                try {
                    data = callback.execute(param);
                    result = ApiResult.ok(data);
                } catch (CommonException e) {
                    result = ApiResult.fail(e.getErrorCode(), e.getErrorMessage());
                } catch (DataIntegrityViolationException e) {
                    result = ApiResult.fail(CommonErrorCode.PARAM_INVALID, "数据不合法：必填字段缺失或违反数据约束");
                } catch (Exception e) {
                    LoggerUtil.error(LogFileEnum.COMMON_ERROR, e, "执行{}业务逻辑时抛出异常", caller);
                    result = ApiResult.fail(CommonErrorCode.SYSTEM_ERROR);
                }
            }
        } finally {
            try {
                callback.afterService(param, data);
            } catch (Exception e) {
                LoggerUtil.error(LogFileEnum.COMMON_ERROR, e, "afterService 执行异常 caller={}", caller);
            }
            // 请求摘要（common-digest.log）：时间/traceId 由日志 pattern 输出，这里只记基础信息 + 入参 + 最终返回值（非异常）
            LoggerUtil.info(LogFileEnum.COMMON_DIGEST,
                    "请求摘要 接口信息={} 时间={} 请求参数={} 返回值={}",
                    caller, startTime, param, result);
        }
        return result;
    }

    /**
     * 执行无返回值的 web 用例（如删除），回调无需 return。
     *
     * @param param    入参（DTO 或路径参数）
     * @param callback 无返回值业务回调
     * @param <P>      入参类型
     * @return 统一返回体
     */
    public static <P> ApiResult<Void> executeWithoutResult(P param, CallbackWithoutResult<P> callback) {
        return execute(param, new Callback<P, Void>() {

            @Override
            public void beforeService(P p) {
                callback.beforeService(p);
            }

            @Override
            public Void execute(P p) {
                callback.execute(p);
                return null;
            }

            @Override
            public void afterService(P p, Void result) {
                callback.afterService(p);
            }
        });
    }

    /**
     * 业务回调：Controller 用匿名类实现三个钩子。
     *
     * @param <P> 入参类型
     * @param <R> 出参类型
     */
    public interface Callback<P, R> {

        /** 业务执行前钩子：统一在此调用 {@link ParamValidator#validate(Object, Class[])} 做参数校验。无参数用例无需重写。 */
        default void beforeService(P param) {
        }

        /** 核心业务逻辑。 */
        R execute(P param);

        /** 业务执行后钩子：留空即可，如需清理/日志在此实现。 */
        default void afterService(P param, R result) {}
    }

    /**
     * 无返回值业务回调：用于 {@link #executeWithoutResult(Object, CallbackWithoutResult)}。
     *
     * @param <P> 入参类型
     */
    public interface CallbackWithoutResult<P> {

        /** 业务执行前钩子：统一在此调用 {@link ParamValidator#validate(Object, Class[])} 做参数校验。无参数用例无需重写。 */
        default void beforeService(P param) {
        }

        /** 核心业务逻辑（无返回值）。 */
        void execute(P param);

        /** 业务执行后钩子：留空即可，如需清理/日志在此实现。 */
        default void afterService(P param){};
    }

    /** 解析调用方（Controller 类名.方法名），用于接口信息日志。
     *  每次请求遍历堆栈，当前量级可接受；如需精确接口名可改为显式传参。 */
    private static String resolveCaller() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stack) {
            String className = element.getClassName();
            if (!className.startsWith(ApiTemplate.class.getName())
                    && !className.startsWith("java.")
                    && !className.startsWith("jdk.")) {
                return className.substring(className.lastIndexOf('.') + 1) + "." + element.getMethodName();
            }
        }
        return "unknown";
    }
}
