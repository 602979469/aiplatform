package com.jakt.aiplatform.web.result;
import com.jakt.aiplatform.core.model.enums.LoginLogStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
/**
 * 登录记录响应。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AuthLoginLogResponse extends BaseResult {
    /** 日志ID。 */
    private Long logId;
    /** 用户ID。 */
    private Long userId;
    /** 用户名。 */
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
