package com.jakt.aiplatform.core.model.domain;

import java.util.List;
import com.jakt.aiplatform.core.model.enums.GenTplCategoryEnum;
import com.jakt.aiplatform.core.model.enums.GenTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 代码生成表领域模型（RuoYi 结构：继承 BaseEntity，含组装字段 pkColumn/subTable/columns 等）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GenTable extends BaseEntity {

    /** 编号。 */
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

    /** 使用的模板（crud单表 tree树表 sub主子表）。 */
    private GenTplCategoryEnum tplCategory;

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
    private GenTypeEnum genType;

    /** 生成路径（不填默认项目路径）。 */
    private String genPath;

    /** 主键信息（组装字段）。 */
    private GenTableColumn pkColumn;

    /** 子表信息（组装字段）。 */
    private GenTable subTable;

    /** 表列信息（组装字段）。 */
    private List<GenTableColumn> columns;

    /** 其它生成选项。 */
    private String options;

    /** 树编码字段（组装字段）。 */
    private String treeCode;

    /** 树父编码字段（组装字段）。 */
    private String treeParentCode;

    /** 树名称字段（组装字段）。 */
    private String treeName;

    /** 上级菜单ID（组装字段）。 */
    private String parentMenuId;

    /** 上级菜单名称（组装字段）。 */
    private String parentMenuName;

    /** 是否视图（组装字段）。 */
    private boolean isView;
}
