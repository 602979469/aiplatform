package com.jakt.aiplatform.common.dal.dataobject;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 代码生成 DO对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GenTableDO extends BaseDO {
    /** 主键。 */
    private Long tableId;

    /** 表名称。 */
    private String tableName;

    /** 表描述。 */
    private String tableComment;

    /** 关联子表的表名。 */
    private String subTableName;

    /** 子表关联的外键名。 */
    private String subTableFkName;

    /** 实体类名称。 */
    private String className;

    /** 使用的模板（crud单表操作 tree树表操作 sub主子表操作）。 */
    private String tplCategory;

    /** 生成包路径。 */
    private String packageName;

    /** 生成模块名。 */
    private String moduleName;

    /** 生成业务名。 */
    private String businessName;

    /** 生成功能名。 */
    private String functionName;

    /** 生成功能作者。 */
    private String functionAuthor;

    /** 表单布局（单列 双列 三列）。 */
    private Integer formColNum;

    /** 生成代码方式（0zip压缩包 1自定义路径）。 */
    private String genType;

    /** 生成路径（不填默认项目路径）。 */
    private String genPath;

    /** 其它生成选项。 */
    private String options;

    /** 备注。 */
    private String remark;

    /** 创建者。 */
    private String createBy;

    /** 更新者。 */
    private String updateBy;

}
