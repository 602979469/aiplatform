package com.jakt.aiplatform.web.param;

import java.time.LocalDateTime;
import jakarta.validation.constraints.Size;
import com.jakt.aiplatform.core.model.enums.LoginStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 更新登录日志请求 DTO。
 *
 * <p>校验规则与 sys_logininfor 表字段对齐：非空 + varchar 长度，不做业务自定义规则。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysLogininforUpdateRequest extends BaseRequest {
    /** 登录账号。 */
    @Size(max = 50, message = "登录账号长度不能超过 50")
    private String loginName;

    /** 登录IP地址。 */
    @Size(max = 128, message = "登录IP地址长度不能超过 128")
    private String ipaddr;

    /** 登录地点。 */
    @Size(max = 255, message = "登录地点长度不能超过 255")
    private String loginLocation;

    /** 浏览器类型。 */
    @Size(max = 50, message = "浏览器类型长度不能超过 50")
    private String browser;

    /** 操作系统。 */
    @Size(max = 50, message = "操作系统长度不能超过 50")
    private String os;

    /** 登录状态（0成功 1失败）。 */
    private LoginStatusEnum status;

    /** 提示消息。 */
    @Size(max = 255, message = "提示消息长度不能超过 255")
    private String msg;

    /** 访问时间。 */
    private LocalDateTime loginTime;

}
