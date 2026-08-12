package com.jakt.aiplatform.web.param;
import java.time.LocalDateTime;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 创建在线用户请求 DTO。
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserOnlineCreateRequest extends BaseRequest {

    /** sessionId。 */
    @NotBlank(message = "sessionId不能为空")
    private String sessionId;

    /** 登录账号。 */
    @Size(max = 50, message = "登录账号长度不能超过 50")
    private String loginName;

    /** 部门名称。 */
    @Size(max = 50, message = "部门名称长度不能超过 50")
    private String deptName;

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

    /** 在线状态on_line在线off_line离线。 */
    @Size(max = 10, message = "在线状态on_line在线off_line离线长度不能超过 10")
    private String status;

    /** session创建时间。 */
    private LocalDateTime startTimestamp;

    /** session最后访问时间。 */
    private LocalDateTime lastAccessTime;

    /** 超时时间，单位为分钟。 */
    private Long expireTime;

    /** 序列化的Session数据，用于服务重启后恢复会话。 */
    @Size(max = 65535, message = "序列化的Session数据，用于服务重启后恢复会话长度不能超过 65535")
    private String sessionData;

}
