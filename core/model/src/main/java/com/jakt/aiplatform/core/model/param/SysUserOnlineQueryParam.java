package com.jakt.aiplatform.core.model.param;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 在线用户查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserOnlineQueryParam extends PageParam {

    /** 主键。 */
    private String sessionId;

    /** 登录账号。 */
    private String loginName;

    /** 部门名称。 */
    private String deptName;

    /** 登录IP地址。 */
    private String ipaddr;

    /** 登录地点。 */
    private String loginLocation;

    /** 浏览器类型。 */
    private String browser;

    /** 操作系统。 */
    private String os;

    /** 在线状态on_line在线off_line离线。 */
    private String status;

    /** session创建时间。 */
    private LocalDateTime startTimestamp;

    /** session最后访问时间。 */
    private LocalDateTime lastAccessTime;

    /** 超时时间，单位为分钟。 */
    private Integer expireTime;

    /** 序列化的Session数据，用于服务重启后恢复会话。 */
    private String sessionData;

    /** 创建时间起。 */
    private LocalDateTime createTimeBegin;

    /** 创建时间止。 */
    private LocalDateTime createTimeEnd;

    /** 更新时间起。 */
    private LocalDateTime updateTimeBegin;

    /** 更新时间止。 */
    private LocalDateTime updateTimeEnd;

}
