package com.jakt.aiplatform.web.param;
import java.time.LocalDateTime;
import jakarta.validation.constraints.Size;
import com.jakt.aiplatform.core.model.enums.BusinessTypeEnum;
import com.jakt.aiplatform.core.model.enums.OperatorTypeEnum;
import com.jakt.aiplatform.core.model.enums.BusinessStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 创建操作日志请求 DTO。
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysOperLogCreateRequest extends BaseRequest {

    /** 模块标题。 */
    @Size(max = 50, message = "模块标题长度不能超过 50")
    private String title;

    /** 业务类型（0其它 1新增 2修改 3删除）。 */
    private BusinessTypeEnum businessType;

    /** 方法名称。 */
    @Size(max = 200, message = "方法名称长度不能超过 200")
    private String method;

    /** 请求方式。 */
    @Size(max = 10, message = "请求方式长度不能超过 10")
    private String requestMethod;

    /** 操作类别（0其它 1后台用户 2手机端用户）。 */
    private OperatorTypeEnum operatorType;

    /** 操作人员。 */
    @Size(max = 50, message = "操作人员长度不能超过 50")
    private String operName;

    /** 部门名称。 */
    @Size(max = 50, message = "部门名称长度不能超过 50")
    private String deptName;

    /** 请求URL。 */
    @Size(max = 255, message = "请求URL长度不能超过 255")
    private String operUrl;

    /** 主机地址。 */
    @Size(max = 128, message = "主机地址长度不能超过 128")
    private String operIp;

    /** 操作地点。 */
    @Size(max = 255, message = "操作地点长度不能超过 255")
    private String operLocation;

    /** 请求参数。 */
    @Size(max = 2000, message = "请求参数长度不能超过 2000")
    private String operParam;

    /** 返回参数。 */
    @Size(max = 2000, message = "返回参数长度不能超过 2000")
    private String jsonResult;

    /** 操作状态（0正常 1异常）。 */
    private BusinessStatusEnum status;

    /** 错误消息。 */
    @Size(max = 2000, message = "错误消息长度不能超过 2000")
    private String errorMsg;

    /** 操作时间。 */
    private LocalDateTime operTime;

    /** 消耗时间。 */
    private Long costTime;

}
