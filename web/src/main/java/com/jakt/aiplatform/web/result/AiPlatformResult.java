package com.jakt.aiplatform.web.result;

import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.exception.ErrorCode;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * web 层统一返回体（RuoYi AjaxResult 语义：code + msg + data）。
 *
 * <p>成功 code=0；警告 code=301；失败 code=ErrorCodeEnum 数字值或 500。
 * ok/fail 为模板与既有调用保留的工厂别名，success/error/warn 为 AjaxResult 风格工厂。
 */
@Data
public class AiPlatformResult<T> implements Serializable {

    /** 序列化版本号。 */
    @Serial
    private static final long serialVersionUID = 1L;

    /** 成功状态码。 */
    public static final int SUCCESS_CODE = 0;

    /** 警告状态码。 */
    public static final int WARN_CODE = 301;

    /** 错误状态码。 */
    public static final int ERROR_CODE = 500;

    /** 状态码。 */
    private int code = SUCCESS_CODE;

    /** 返回消息。 */
    private String msg;

    /** 业务数据。 */
    private T data;

    /**
     * 成功（无数据）。
     *
     * @return 成功结果
     */
    public static <T> AiPlatformResult<T> ok() {
        return success();
    }

    /**
     * 成功（带数据）。
     *
     * @param data 业务数据
     * @return 成功结果
     */
    public static <T> AiPlatformResult<T> ok(T data) {
        return success(data);
    }

    /**
     * 失败（按错误码）。
     *
     * @param errorCodeEnum 错误码
     * @return 失败结果
     */
    public static <T> AiPlatformResult<T> fail(ErrorCodeEnum errorCodeEnum) {
        return fail(errorCodeEnum, errorCodeEnum.getMessage());
    }

    /**
     * 失败（按错误码 + 消息）。
     *
     * @param errorCodeEnum 错误码
     * @param message       消息
     * @return 失败结果
     */
    public static <T> AiPlatformResult<T> fail(ErrorCodeEnum errorCodeEnum, String message) {
        AiPlatformResult<T> result = new AiPlatformResult<>();
        result.code = errorCodeEnum.getCode();
        result.msg = message;
        return result;
    }

    /**
     * 失败（兼容 ErrorCode 接口入参）。
     *
     * @param errorCode 错误码
     * @param message   消息
     * @return 失败结果
     */
    public static <T> AiPlatformResult<T> fail(ErrorCode errorCode, String message) {
        if (errorCode instanceof ErrorCodeEnum errorCodeEnum) {
            return fail(errorCodeEnum, message);
        }
        return fail(ErrorCodeEnum.BIZ_ERROR, message);
    }

    /**
     * 成功（无数据，AjaxResult 风格）。
     *
     * @return 成功结果
     */
    public static <T> AiPlatformResult<T> success() {
        return success("操作成功");
    }

    /**
     * 成功（带数据，AjaxResult 风格）。
     *
     * @param data 业务数据
     * @return 成功结果
     */
    public static <T> AiPlatformResult<T> success(T data) {
        return success("操作成功", data);
    }

    /**
     * 成功（自定义消息，AjaxResult 风格）。
     *
     * @param msg 消息
     * @return 成功结果
     */
    public static <T> AiPlatformResult<T> success(String msg) {
        return success(msg, null);
    }

    /**
     * 成功（自定义消息 + 数据，AjaxResult 风格）。
     *
     * @param msg  消息
     * @param data 业务数据
     * @return 成功结果
     */
    public static <T> AiPlatformResult<T> success(String msg, T data) {
        AiPlatformResult<T> result = new AiPlatformResult<>();
        result.code = SUCCESS_CODE;
        result.msg = msg;
        result.data = data;
        return result;
    }

    /**
     * 警告（AjaxResult 风格）。
     *
     * @param msg 消息
     * @return 警告结果
     */
    public static <T> AiPlatformResult<T> warn(String msg) {
        return warn(msg, null);
    }

    /**
     * 警告（AjaxResult 风格）。
     *
     * @param msg  消息
     * @param data 业务数据
     * @return 警告结果
     */
    public static <T> AiPlatformResult<T> warn(String msg, T data) {
        AiPlatformResult<T> result = new AiPlatformResult<>();
        result.code = WARN_CODE;
        result.msg = msg;
        result.data = data;
        return result;
    }

    /**
     * 失败（AjaxResult 风格）。
     *
     * @return 失败结果
     */
    public static <T> AiPlatformResult<T> error() {
        return error("操作失败");
    }

    /**
     * 失败（AjaxResult 风格）。
     *
     * @param msg 消息
     * @return 失败结果
     */
    public static <T> AiPlatformResult<T> error(String msg) {
        return error(msg, null);
    }

    /**
     * 失败（AjaxResult 风格）。
     *
     * @param msg  消息
     * @param data 业务数据
     * @return 失败结果
     */
    public static <T> AiPlatformResult<T> error(String msg, T data) {
        AiPlatformResult<T> result = new AiPlatformResult<>();
        result.code = ERROR_CODE;
        result.msg = msg;
        result.data = data;
        return result;
    }

    /**
     * 是否成功。
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return code == SUCCESS_CODE;
    }

    /**
     * 是否警告。
     *
     * @return 是否警告
     */
    public boolean isWarn() {
        return code == WARN_CODE;
    }

    /**
     * 是否失败。
     *
     * @return 是否失败
     */
    public boolean isError() {
        return code == ERROR_CODE;
    }
}
