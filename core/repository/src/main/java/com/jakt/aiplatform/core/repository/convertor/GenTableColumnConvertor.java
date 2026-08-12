package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.GenTableColumnDO;
import com.jakt.aiplatform.core.model.domain.GenTableColumn;


/**
 * 代码生成字段 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class GenTableColumnConvertor {

    private GenTableColumnConvertor() {
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
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
