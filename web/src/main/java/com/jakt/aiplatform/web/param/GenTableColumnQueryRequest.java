package com.jakt.aiplatform.web.param;

import java.time.LocalDateTime;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 代码生成字段查询请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GenTableColumnQueryRequest extends BaseRequest {

    /** 主键。 */
    private Long columnId;

    /** 归属表编号。 */
    private Long tableId;

    /** 列名称。 */
    private String columnName;

    /** 列描述。 */
    private String columnComment;

    /** 列类型。 */
    private String columnType;

    /** JAVA类型。 */
    private String javaType;

    /** JAVA字段名。 */
    private String javaField;

    /** 是否主键（1是）。 */
    private String isPk;

    /** 是否自增（1是）。 */
    private String isIncrement;

    /** 是否必填（1是）。 */
    private String isRequired;

    /** 是否为插入字段（1是）。 */
    private String isInsert;

    /** 是否编辑字段（1是）。 */
    private String isEdit;

    /** 是否列表字段（1是）。 */
    private String isList;

    /** 是否查询字段（1是）。 */
    private String isQuery;

    /** 查询方式（等于、不等于、大于、小于、范围）。 */
    private String queryType;

    /** 显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）。 */
    private String htmlType;

    /** 字典类型。 */
    private String dictType;

    /** 排序。 */
    private Integer sort;

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
