package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.Size;
import com.jakt.aiplatform.core.model.enums.GenTplCategoryEnum;
import com.jakt.aiplatform.core.model.enums.GenTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 更新代码生成请求 DTO。
 *
 * <p>校验规则与 gen_table 表字段对齐：非空 + varchar 长度，不做业务自定义规则。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GenTableUpdateRequest extends BaseRequest {
    /** 表名称。 */
    @Size(max = 200, message = "表名称长度不能超过 200")
    private String tableName;

    /** 表描述。 */
    @Size(max = 500, message = "表描述长度不能超过 500")
    private String tableComment;

    /** 关联子表的表名。 */
    @Size(max = 64, message = "关联子表的表名长度不能超过 64")
    private String subTableName;

    /** 子表关联的外键名。 */
    @Size(max = 64, message = "子表关联的外键名长度不能超过 64")
    private String subTableFkName;

    /** 实体类名称。 */
    @Size(max = 100, message = "实体类名称长度不能超过 100")
    private String className;

    /** 使用的模板（crud单表操作 tree树表操作 sub主子表操作）。 */
    private GenTplCategoryEnum tplCategory;

    /** 生成包路径。 */
    @Size(max = 100, message = "生成包路径长度不能超过 100")
    private String packageName;

    /** 生成模块名。 */
    @Size(max = 30, message = "生成模块名长度不能超过 30")
    private String moduleName;

    /** 生成业务名。 */
    @Size(max = 30, message = "生成业务名长度不能超过 30")
    private String businessName;

    /** 生成功能名。 */
    @Size(max = 50, message = "生成功能名长度不能超过 50")
    private String functionName;

    /** 生成功能作者。 */
    @Size(max = 50, message = "生成功能作者长度不能超过 50")
    private String functionAuthor;

    /** 表单布局（单列 双列 三列）。 */
    private Integer formColNum;

    /** 生成代码方式（0zip压缩包 1自定义路径）。 */
    private GenTypeEnum genType;

    /** 生成路径（不填默认项目路径）。 */
    @Size(max = 200, message = "生成路径（不填默认项目路径）长度不能超过 200")
    private String genPath;

    /** 其它生成选项。 */
    @Size(max = 1000, message = "其它生成选项长度不能超过 1000")
    private String options;

    /** 备注。 */
    @Size(max = 500, message = "备注长度不能超过 500")
    private String remark;

}
