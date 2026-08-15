package com.jakt.aiplatform.common.dal.dataobject;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 登录记录表 DO对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthLoginLogDO extends BaseDO {
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
    private String status;

    /** 说明。 */
    private String message;

    /** 事件时间。 */
    private LocalDateTime loginTime;

}
