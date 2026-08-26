package com.jakt.aiplatform.core.model.domain;
import com.jakt.aiplatform.common.framework.model.BaseModel;

import com.jakt.aiplatform.core.model.enums.LoginLogStatusEnum;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 登录记录表领域模型。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthLoginLog extends BaseModel {
    /** 主键。 */
    private Long logId;

    /** 用户ID（失败时可能为空）。 */
    private Long userId;

    /** 登录账号。 */
    private String username;

    /** 登录IP。 */
    private String loginIp;

    /** 浏览器UA。 */
    private String userAgent;

    /** 结果（0成功 1失败 2被踢 3被顶 4注销）。 */
    private LoginLogStatusEnum status;

    /** 说明。 */
    private String message;

    /** 事件时间。 */
    private LocalDateTime loginTime;

}
