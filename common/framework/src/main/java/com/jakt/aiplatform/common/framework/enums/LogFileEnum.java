package com.jakt.aiplatform.common.framework.enums;

import lombok.Getter;

/**
 * 日志文件枚举。
 */
@Getter
public enum LogFileEnum {

    /** 通用错误日志。 */
    COMMON_ERROR("common-error"),

    /** 请求摘要日志（ApiTemplate 统一打印 入参/返回值）。 */
    COMMON_DIGEST("common-digest"),

    /** 业务服务日志。 */
    BIZ_SERVICE("biz-service"),

    /** 异步调度日志。 */
    ASYNC_SCHEDULE("async-schedule"),

    /** 外部集成日志。 */
    INTEGRATION("integration");

    private final String fileName;

    LogFileEnum(String fileName) {
        this.fileName = fileName;
    }
}
