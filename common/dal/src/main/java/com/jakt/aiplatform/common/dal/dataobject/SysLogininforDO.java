package com.jakt.aiplatform.common.dal.dataobject;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 登录日志 DO对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysLogininforDO extends BaseDO {
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

}
