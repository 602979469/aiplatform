package com.jakt.aiplatform.web.param;

import java.time.LocalDateTime;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 操作日志查询请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysOperLogQueryRequest extends BaseRequest {

    /** 主键。 */
    private Long operId;

    /** 模块标题。 */
    private String title;

    /** 业务类型（0其它 1新增 2修改 3删除）。 */
    private Integer businessType;

    /** 方法名称。 */
    private String method;

    /** 请求方式。 */
    private String requestMethod;

    /** 操作类别（0其它 1后台用户 2手机端用户）。 */
    private Integer operatorType;

    /** 操作人员。 */
    private String operName;

    /** 部门名称。 */
    private String deptName;

    /** 请求URL。 */
    private String operUrl;

    /** 主机地址。 */
    private String operIp;

    /** 操作地点。 */
    private String operLocation;

    /** 请求参数。 */
    private String operParam;

    /** 返回参数。 */
    private String jsonResult;

    /** 操作状态（0正常 1异常）。 */
    private Integer status;

    /** 错误消息。 */
    private String errorMsg;

    /** 操作时间。 */
    private LocalDateTime operTime;

    /** 消耗时间。 */
    private Long costTime;

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
