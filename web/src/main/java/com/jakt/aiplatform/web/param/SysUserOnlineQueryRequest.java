package com.jakt.aiplatform.web.param;

import java.time.LocalDateTime;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 在线用户查询请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserOnlineQueryRequest extends BaseRequest {

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

    /** 页码，从 1 开始。 */
    @Min(value = 1, message = "页码不能小于 1")
    private Integer pageNum = 1;

    /** 每页条数。 */
    @Min(value = 1, message = "每页条数不能小于 1")
    @Max(value = 100, message = "每页条数不能超过 100")
    private Integer pageSize = 10;
}
