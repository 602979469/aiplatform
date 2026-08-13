package com.jakt.aiplatform.core.model.param;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 登录日志查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysLogininforQueryParam extends PageQueryParam {

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
    private String status;

    /** 提示消息。 */
    private String msg;

    /** 访问时间。 */
    private LocalDateTime loginTime;

    /** 创建时间起。 */
    private LocalDateTime createTimeBegin;

    /** 创建时间止。 */
    private LocalDateTime createTimeEnd;

    /** 更新时间起。 */
    private LocalDateTime updateTimeBegin;

    /** 更新时间止。 */
    private LocalDateTime updateTimeEnd;

}
