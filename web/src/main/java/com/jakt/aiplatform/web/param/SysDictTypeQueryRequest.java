package com.jakt.aiplatform.web.param;

import java.time.LocalDateTime;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典类型查询请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysDictTypeQueryRequest extends BaseRequest {

    /** 主键。 */
    private Long dictId;

    /** 字典名称。 */
    private String dictName;

    /** 字典类型。 */
    private String dictType;

    /** 状态（0正常 1停用）。 */
    private String status;

    /** 备注。 */
    private String remark;

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
