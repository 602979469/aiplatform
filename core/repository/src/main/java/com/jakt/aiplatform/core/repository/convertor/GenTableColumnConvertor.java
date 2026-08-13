package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.GenTableColumnDO;
import com.jakt.aiplatform.core.model.domain.GenTableColumn;
import com.jakt.aiplatform.core.model.param.GenTableColumnQueryParam;

/**
 * 代码生成字段 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class GenTableColumnConvertor {

    private GenTableColumnConvertor() {
    }

    /**
     * 领域模型 → 查询参数（显式赋值）。
     *
     * @param genTableColumn 代码生成字段领域模型
     * @return 代码生成字段查询参数
     */
    public static GenTableColumnQueryParam toQueryParam(GenTableColumn genTableColumn) {
        GenTableColumnQueryParam query = new GenTableColumnQueryParam();
        query.setColumnId(genTableColumn.getColumnId());
        query.setTableId(genTableColumn.getTableId());
        query.setColumnName(genTableColumn.getColumnName());
        query.setColumnComment(genTableColumn.getColumnComment());
        query.setColumnType(genTableColumn.getColumnType());
        query.setJavaType(genTableColumn.getJavaType());
        query.setJavaField(genTableColumn.getJavaField());
        query.setIsPk(genTableColumn.getIsPk());
        query.setIsIncrement(genTableColumn.getIsIncrement());
        query.setIsRequired(genTableColumn.getIsRequired());
        query.setIsInsert(genTableColumn.getIsInsert());
        query.setIsEdit(genTableColumn.getIsEdit());
        query.setIsList(genTableColumn.getIsList());
        query.setIsQuery(genTableColumn.getIsQuery());
        query.setQueryType(genTableColumn.getQueryType());
        query.setHtmlType(genTableColumn.getHtmlType());
        query.setDictType(genTableColumn.getDictType());
        query.setSort(genTableColumn.getSort());
        return query;
    }

    /**
     * 数据对象 → 查询参数（显式赋值，仅拷贝查询相关字段）。
     *
     * @param condition 代码生成字段数据对象（条件载体）
     * @return 代码生成字段查询参数
     */
    public static GenTableColumnQueryParam toQueryParam(GenTableColumnDO condition) {
        GenTableColumnQueryParam query = new GenTableColumnQueryParam();
        query.setColumnId(condition.getColumnId());
        query.setTableId(condition.getTableId());
        query.setColumnName(condition.getColumnName());
        query.setColumnComment(condition.getColumnComment());
        query.setColumnType(condition.getColumnType());
        query.setJavaType(condition.getJavaType());
        query.setJavaField(condition.getJavaField());
        query.setIsPk(condition.getIsPk());
        query.setIsIncrement(condition.getIsIncrement());
        query.setIsRequired(condition.getIsRequired());
        query.setIsInsert(condition.getIsInsert());
        query.setIsEdit(condition.getIsEdit());
        query.setIsList(condition.getIsList());
        query.setIsQuery(condition.getIsQuery());
        query.setQueryType(condition.getQueryType());
        query.setHtmlType(condition.getHtmlType());
        query.setDictType(condition.getDictType());
        query.setSort(condition.getSort());
        return query;
    }

    /**
     * DO → 领域模型。
     *
     * @param genTableColumnDO 代码生成字段数据对象；为空返回 null
     * @return 代码生成字段领域模型
     */
    public static GenTableColumn toModel(GenTableColumnDO source) {
        if (source == null) {
            return null;
        }
        GenTableColumn target = new GenTableColumn();
        target.setColumnId(source.getColumnId());
        target.setTableId(source.getTableId());
        target.setColumnName(source.getColumnName());
        target.setColumnComment(source.getColumnComment());
        target.setColumnType(source.getColumnType());
        target.setJavaType(source.getJavaType());
        target.setJavaField(source.getJavaField());
        target.setIsPk(source.getIsPk());
        target.setIsIncrement(source.getIsIncrement());
        target.setIsRequired(source.getIsRequired());
        target.setIsInsert(source.getIsInsert());
        target.setIsEdit(source.getIsEdit());
        target.setIsList(source.getIsList());
        target.setIsQuery(source.getIsQuery());
        target.setQueryType(source.getQueryType());
        target.setHtmlType(source.getHtmlType());
        target.setDictType(source.getDictType());
        target.setSort(source.getSort());
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param genTableColumn 代码生成字段领域模型
     * @return 代码生成字段数据对象
     */
    public static GenTableColumnDO toDO(GenTableColumn source) {
        GenTableColumnDO target = new GenTableColumnDO();
        target.setColumnId(source.getColumnId());
        target.setTableId(source.getTableId());
        target.setColumnName(source.getColumnName());
        target.setColumnComment(source.getColumnComment());
        target.setColumnType(source.getColumnType());
        target.setJavaType(source.getJavaType());
        target.setJavaField(source.getJavaField());
        target.setIsPk(source.getIsPk());
        target.setIsIncrement(source.getIsIncrement());
        target.setIsRequired(source.getIsRequired());
        target.setIsInsert(source.getIsInsert());
        target.setIsEdit(source.getIsEdit());
        target.setIsList(source.getIsList());
        target.setIsQuery(source.getIsQuery());
        target.setQueryType(source.getQueryType());
        target.setHtmlType(source.getHtmlType());
        target.setDictType(source.getDictType());
        target.setSort(source.getSort());
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
