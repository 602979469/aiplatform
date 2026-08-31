package com.jakt.aiplatform.core.model.enums;

import com.jakt.aiplatform.common.framework.error.ErrorCode;

/**
 * 平台业务错误码枚举：业务系统自定义错误码统一收口，禁止写入 common-framework。
 * 通用错误码（系统异常 / 认证异常 / 参数异常）由 framework 的 ErrorCodeEnum 提供。
 */
public enum BizErrorCodeEnum implements ErrorCode {

    /** 资源不存在。 */
    RESOURCE_NOT_FOUND("资源不存在"),

    /** 更新失败：记录不存在或已被修改。 */
    UPDATE_FAILED("更新失败"),

    /** 删除失败：记录不存在或已被删除。 */
    DELETE_FAILED("删除失败"),

    /** 枚举值未匹配。 */
    ENUM_NOT_MATCHED("枚举值未匹配"),

    /** 查询结果不唯一。 */
    RESULT_NOT_UNIQUE("查询结果不唯一"),

    /** 外部服务调用失败。 */
    EXTERNAL_ERROR("外部服务调用失败"),

    /** DeepSeek 接口调用失败。 */
    DEEPSEEK_API_ERROR("DeepSeek 接口调用失败"),

    /** 镜像加速器接口调用失败。 */
    XUANYUAN_API_ERROR("镜像加速器接口调用失败"),

    /** 外部服务调用超时。 */
    TIMEOUT("外部服务调用超时"),

    /** 未登录或登录已过期（HTTP 401）。 */
    NOT_LOGIN("未登录或登录已过期"),

    /** 无权限访问（HTTP 403）。 */
    NO_PERMISSION("无权限访问"),

    /** 用户名已存在。 */
    USERNAME_EXISTS("用户名已存在"),

    /** 用户名或密码错误（不区分具体哪项错误，防枚举）。 */
    LOGIN_FAILED("用户名或密码错误"),

    /** 原密码错误。 */
    OLD_PASSWORD_ERROR("原密码错误"),

    /** 账号已被停用。 */
    USER_DISABLED("账号已被停用"),

    /** 账号已被封禁。 */
    ACCOUNT_BANNED("账号已被封禁"),

    /** 会话不存在或无权访问。 */
    SESSION_ACCESS_DENIED("会话不存在或无权访问"),

    /** 待重试的消息不存在或无权访问。 */
    CHAT_MESSAGE_NOT_RETRYABLE("待重试的消息不存在或无权访问"),

    /** 角色标识已存在。 */
    ROLE_KEY_EXISTS("角色标识已存在"),

    /** 存在子菜单，禁止删除。 */
    MENU_HAS_CHILDREN("存在子菜单，禁止删除"),

    /** 镜像下载任务不存在。 */
    MIRROR_TASK_NOT_FOUND("镜像下载任务不存在"),

    /** 文件名不合法。 */
    MIRROR_FILE_NAME_INVALID("文件名不合法"),

    /** 镜像文件不存在。 */
    MIRROR_FILE_NOT_FOUND("镜像文件不存在"),

    /** 镜像文件不是普通文件。 */
    MIRROR_FILE_NOT_REGULAR("镜像文件不是普通文件"),

    /** 创建镜像存储目录失败。 */
    MIRROR_DIR_CREATE_FAILED("创建镜像存储目录失败"),

    /** 文件不存在或无权访问。 */
    FILE_NOT_FOUND("文件不存在"),

    /** 文件名不合法。 */
    FILE_NAME_INVALID("文件名不合法"),

    /** 当前状态不允许该操作。 */
    IMAGE_EXISTS("镜像名+版本已存在"),

    STATUS_NOT_ALLOWED("当前状态不允许该操作");

    private final String message;

    BizErrorCodeEnum(String message) {
        this.message = message;
    }

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
