package com.jakt.aiplatform.common.dal.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件信息表查询参数（common-dal 专用）：字段为数据库原始类型，仅供 Mapper/XML 使用。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileInfoDalQuery extends DalPageQuery {

    /** 业务命名空间（隔离维度）。 */
    private String namespace;

    /** 原始文件名（模糊匹配）。 */
    private String originalName;
}
