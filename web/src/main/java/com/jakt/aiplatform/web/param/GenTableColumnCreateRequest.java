package com.jakt.aiplatform.web.param;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 创建代码生成字段请求 DTO。
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GenTableColumnCreateRequest extends BaseRequest {

    /** 归属表编号。 */
    private Long tableId;

    /** 列名称。 */
    @Size(max = 200, message = "列名称长度不能超过 200")
    private String columnName;

    /** 列描述。 */
    @Size(max = 500, message = "列描述长度不能超过 500")
    private String columnComment;

    /** 列类型。 */
    @Size(max = 100, message = "列类型长度不能超过 100")
    private String columnType;

    /** JAVA类型。 */
    @Size(max = 500, message = "JAVA类型长度不能超过 500")
    private String javaType;

    /** JAVA字段名。 */
    @Size(max = 200, message = "JAVA字段名长度不能超过 200")
    private String javaField;

    /** 是否主键（1是）。 */
    @Size(max = 1, message = "是否主键（1是）长度不能超过 1")
    private String isPk;

    /** 是否自增（1是）。 */
    @Size(max = 1, message = "是否自增（1是）长度不能超过 1")
    private String isIncrement;

    /** 是否必填（1是）。 */
    @Size(max = 1, message = "是否必填（1是）长度不能超过 1")
    private String isRequired;

    /** 是否为插入字段（1是）。 */
    @Size(max = 1, message = "是否为插入字段（1是）长度不能超过 1")
    private String isInsert;

    /** 是否编辑字段（1是）。 */
    @Size(max = 1, message = "是否编辑字段（1是）长度不能超过 1")
    private String isEdit;

    /** 是否列表字段（1是）。 */
    @Size(max = 1, message = "是否列表字段（1是）长度不能超过 1")
    private String isList;

    /** 是否查询字段（1是）。 */
    @Size(max = 1, message = "是否查询字段（1是）长度不能超过 1")
    private String isQuery;

    /** 查询方式（等于、不等于、大于、小于、范围）。 */
    @Size(max = 200, message = "查询方式（等于、不等于、大于、小于、范围）长度不能超过 200")
    private String queryType;

    /** 显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）。 */
    @Size(max = 200, message = "显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）长度不能超过 200")
    private String htmlType;

    /** 字典类型。 */
    @Size(max = 200, message = "字典类型长度不能超过 200")
    private String dictType;

    /** 排序。 */
    private Integer sort;

}
