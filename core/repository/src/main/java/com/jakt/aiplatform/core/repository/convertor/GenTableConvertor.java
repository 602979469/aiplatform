package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.GenTableDO;
import com.jakt.aiplatform.core.model.domain.GenTable;
import com.jakt.aiplatform.core.model.enums.GenTplCategoryEnum;
import com.jakt.aiplatform.core.model.enums.GenTypeEnum;
import com.jakt.aiplatform.core.model.param.GenTableQueryParam;
import cn.hutool.core.util.ObjectUtil;

/**
 * 代码生成 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class GenTableConvertor {

    private GenTableConvertor() {
    }

    /**
     * 领域模型 → 查询参数（枚举转 code，显式赋值）。
     *
     * @param genTable 代码生成领域模型
     * @return 代码生成查询参数
     */
    public static GenTableQueryParam toQueryParam(GenTable genTable) {
        GenTableQueryParam query = new GenTableQueryParam();
        query.setTableId(genTable.getTableId());
        query.setTableName(genTable.getTableName());
        query.setTableComment(genTable.getTableComment());
        query.setSubTableName(genTable.getSubTableName());
        query.setSubTableFkName(genTable.getSubTableFkName());
        query.setClassName(genTable.getClassName());
        query.setTplCategory(genTable.getTplCategory() == null ? null : genTable.getTplCategory().getCode());
        query.setPackageName(genTable.getPackageName());
        query.setModuleName(genTable.getModuleName());
        query.setBusinessName(genTable.getBusinessName());
        query.setFunctionName(genTable.getFunctionName());
        query.setFunctionAuthor(genTable.getFunctionAuthor());
        query.setFormColNum(genTable.getFormColNum());
        query.setGenType(genTable.getGenType() == null ? null : genTable.getGenType().getCode());
        query.setGenPath(genTable.getGenPath());
        query.setOptions(genTable.getOptions());
        query.setRemark(genTable.getRemark());
        return query;
    }

    /**
     * 数据对象 → 查询参数（显式赋值，仅拷贝查询相关字段）。
     *
     * @param condition 代码生成数据对象（条件载体）
     * @return 代码生成查询参数
     */
    public static GenTableQueryParam toQueryParam(GenTableDO condition) {
        GenTableQueryParam query = new GenTableQueryParam();
        query.setTableId(condition.getTableId());
        query.setTableName(condition.getTableName());
        query.setTableComment(condition.getTableComment());
        query.setSubTableName(condition.getSubTableName());
        query.setSubTableFkName(condition.getSubTableFkName());
        query.setClassName(condition.getClassName());
        query.setTplCategory(condition.getTplCategory());
        query.setPackageName(condition.getPackageName());
        query.setModuleName(condition.getModuleName());
        query.setBusinessName(condition.getBusinessName());
        query.setFunctionName(condition.getFunctionName());
        query.setFunctionAuthor(condition.getFunctionAuthor());
        query.setFormColNum(condition.getFormColNum());
        query.setGenType(condition.getGenType());
        query.setGenPath(condition.getGenPath());
        query.setOptions(condition.getOptions());
        query.setRemark(condition.getRemark());
        return query;
    }

    /**
     * DO → 领域模型。
     *
     * @param genTableDO 代码生成数据对象；为空返回 null
     * @return 代码生成领域模型
     */
    public static GenTable toModel(GenTableDO source) {
        if (source == null) {
            return null;
        }
        GenTable target = new GenTable();
        target.setTableId(source.getTableId());
        target.setTableName(source.getTableName());
        target.setTableComment(source.getTableComment());
        target.setSubTableName(source.getSubTableName());
        target.setSubTableFkName(source.getSubTableFkName());
        target.setClassName(source.getClassName());
        target.setTplCategory(GenTplCategoryEnum.fromCode(source.getTplCategory()));
        target.setPackageName(source.getPackageName());
        target.setModuleName(source.getModuleName());
        target.setBusinessName(source.getBusinessName());
        target.setFunctionName(source.getFunctionName());
        target.setFunctionAuthor(source.getFunctionAuthor());
        target.setFormColNum(source.getFormColNum());
        target.setGenType(GenTypeEnum.fromCode(source.getGenType()));
        target.setGenPath(source.getGenPath());
        target.setOptions(source.getOptions());
        target.setRemark(source.getRemark());
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param genTable 代码生成领域模型
     * @return 代码生成数据对象
     */
    public static GenTableDO toDO(GenTable source) {
        GenTableDO target = new GenTableDO();
        target.setTableId(source.getTableId());
        target.setTableName(source.getTableName());
        target.setTableComment(source.getTableComment());
        target.setSubTableName(source.getSubTableName());
        target.setSubTableFkName(source.getSubTableFkName());
        target.setClassName(source.getClassName());
        target.setTplCategory(ObjectUtil.isNull(source.getTplCategory()) ? null : source.getTplCategory().getCode());
        target.setPackageName(source.getPackageName());
        target.setModuleName(source.getModuleName());
        target.setBusinessName(source.getBusinessName());
        target.setFunctionName(source.getFunctionName());
        target.setFunctionAuthor(source.getFunctionAuthor());
        target.setFormColNum(source.getFormColNum());
        target.setGenType(ObjectUtil.isNull(source.getGenType()) ? null : source.getGenType().getCode());
        target.setGenPath(source.getGenPath());
        target.setOptions(source.getOptions());
        target.setRemark(source.getRemark());
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
