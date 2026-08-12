package com.jakt.aiplatform.web.result;

import java.time.LocalDateTime;
import com.jakt.aiplatform.core.model.enums.LoginStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 登录日志响应 DTO。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SysLogininforResponse extends BaseResult {
    /** 主键。 */
    private Long infoId;

    /** 登录账号。 */
    private String loginName;

    /** 登录IP地址。 */
    private String ipaddr;

    /** 登录地点。 */
    private String loginLocation;

    /** 浏览器类型。 */
    private String browser;

    /** 操作系统。 */
    private String os;

    /** 登录状态（0成功 1失败）。 */
    private LoginStatusEnum status;

    /** 提示消息。 */
    private String msg;

    /** 访问时间。 */
    private LocalDateTime loginTime;

}
