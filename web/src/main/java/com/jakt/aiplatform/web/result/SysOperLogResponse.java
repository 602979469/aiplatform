package com.jakt.aiplatform.web.result;

import java.time.LocalDateTime;
import com.jakt.aiplatform.core.model.enums.BusinessType;
import com.jakt.aiplatform.core.model.enums.OperatorType;
import com.jakt.aiplatform.core.model.enums.BusinessStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 操作日志响应 DTO。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SysOperLogResponse extends BaseResult {
    /** 主键。 */
    private Long operId;

    /** 模块标题。 */
    private String title;

    /** 业务类型（0其它 1新增 2修改 3删除）。 */
    private BusinessType businessType;

    /** 方法名称。 */
    private String method;

    /** 请求方式。 */
    private String requestMethod;

    /** 操作类别（0其它 1后台用户 2手机端用户）。 */
    private OperatorType operatorType;

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
    private BusinessStatus status;

    /** 错误消息。 */
    private String errorMsg;

    /** 操作时间。 */
    private LocalDateTime operTime;

    /** 消耗时间。 */
    private Long costTime;

}
