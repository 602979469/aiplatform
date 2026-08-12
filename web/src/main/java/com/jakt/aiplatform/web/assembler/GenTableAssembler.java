package com.jakt.aiplatform.web.assembler;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.web.param.GenTableCreateRequest;
import com.jakt.aiplatform.web.param.GenTableQueryRequest;
import com.jakt.aiplatform.web.param.GenTableUpdateRequest;
import com.jakt.aiplatform.web.result.GenTableResponse;
import com.jakt.aiplatform.core.model.domain.GenTable;
import com.jakt.aiplatform.core.model.param.GenTableQueryParam;

/**
 * 代码生成对象组装器：DTO 与领域模型互转，只存在于 web。
 */
public final class GenTableAssembler {

    private GenTableAssembler() {
    }

    /**
     * 创建请求 DTO → 领域模型。
     *
     * @param request 创建代码生成请求 DTO
     * @return 代码生成领域模型
     */
    public static GenTable toModel(GenTableCreateRequest request) {
        GenTable genTable = new GenTable();
        genTable.setTableName(request.getTableName());
        genTable.setTableComment(request.getTableComment());
        genTable.setSubTableName(request.getSubTableName());
        genTable.setSubTableFkName(request.getSubTableFkName());
        genTable.setClassName(request.getClassName());
        genTable.setTplCategory(request.getTplCategory());
        genTable.setPackageName(request.getPackageName());
        genTable.setModuleName(request.getModuleName());
        genTable.setBusinessName(request.getBusinessName());
        genTable.setFunctionName(request.getFunctionName());
        genTable.setFunctionAuthor(request.getFunctionAuthor());
        genTable.setFormColNum(request.getFormColNum());
        genTable.setGenType(request.getGenType());
        genTable.setGenPath(request.getGenPath());
        genTable.setOptions(request.getOptions());
        genTable.setRemark(request.getRemark());
        return genTable;
    }

    /**
     * 更新请求 DTO + 路径 ID → 领域模型。
     *
     * @param request 更新代码生成请求 DTO
     * @param id      路径中的代码生成 ID
     * @return 代码生成领域模型
     */
    public static GenTable toModel(GenTableUpdateRequest request, Long id) {
        GenTable genTable = new GenTable();
        genTable.setTableId(id);
        genTable.setTableName(request.getTableName());
        genTable.setTableComment(request.getTableComment());
        genTable.setSubTableName(request.getSubTableName());
        genTable.setSubTableFkName(request.getSubTableFkName());
        genTable.setClassName(request.getClassName());
        genTable.setTplCategory(request.getTplCategory());
        genTable.setPackageName(request.getPackageName());
        genTable.setModuleName(request.getModuleName());
        genTable.setBusinessName(request.getBusinessName());
        genTable.setFunctionName(request.getFunctionName());
        genTable.setFunctionAuthor(request.getFunctionAuthor());
        genTable.setFormColNum(request.getFormColNum());
        genTable.setGenType(request.getGenType());
        genTable.setGenPath(request.getGenPath());
        genTable.setOptions(request.getOptions());
        genTable.setRemark(request.getRemark());
        return genTable;
    }

    /**
     * 查询请求 DTO → 查询参数。
     *
     * @param request 代码生成查询请求 DTO
     * @return 代码生成查询参数
     */
    public static GenTableQueryParam toQueryParam(GenTableQueryRequest request) {
        GenTableQueryParam param = new GenTableQueryParam();
        param.setTableId(request.getTableId());
        param.setTableName(request.getTableName());
        param.setTableComment(request.getTableComment());
        param.setSubTableName(request.getSubTableName());
        param.setSubTableFkName(request.getSubTableFkName());
        param.setClassName(request.getClassName());
        param.setTplCategory(request.getTplCategory());
        param.setPackageName(request.getPackageName());
        param.setModuleName(request.getModuleName());
        param.setBusinessName(request.getBusinessName());
        param.setFunctionName(request.getFunctionName());
        param.setFunctionAuthor(request.getFunctionAuthor());
        param.setFormColNum(request.getFormColNum());
        param.setGenType(request.getGenType());
        param.setGenPath(request.getGenPath());
        param.setOptions(request.getOptions());
        param.setRemark(request.getRemark());
        param.setCreateTimeBegin(request.getCreateTimeBegin());
        param.setCreateTimeEnd(request.getCreateTimeEnd());
        param.setUpdateTimeBegin(request.getUpdateTimeBegin());
        param.setUpdateTimeEnd(request.getUpdateTimeEnd());
        param.setPageNum(ObjectUtil.defaultIfNull(request.getPageNum(), 1));
        param.setPageSize(ObjectUtil.defaultIfNull(request.getPageSize(), 10));
        return param;
    }

    /**
     * 领域模型 → 响应 VO。
     *
     * @param genTable 代码生成领域模型
     * @return 代码生成响应 VO
     */
    public static GenTableResponse toResponse(GenTable genTable) {
        GenTableResponse response = new GenTableResponse();
        response.setTableId(genTable.getTableId());
        response.setTableName(genTable.getTableName());
        response.setTableComment(genTable.getTableComment());
        response.setSubTableName(genTable.getSubTableName());
        response.setSubTableFkName(genTable.getSubTableFkName());
        response.setClassName(genTable.getClassName());
        response.setTplCategory(genTable.getTplCategory());
        response.setPackageName(genTable.getPackageName());
        response.setModuleName(genTable.getModuleName());
        response.setBusinessName(genTable.getBusinessName());
        response.setFunctionName(genTable.getFunctionName());
        response.setFunctionAuthor(genTable.getFunctionAuthor());
        response.setFormColNum(genTable.getFormColNum());
        response.setGenType(genTable.getGenType());
        response.setGenPath(genTable.getGenPath());
        response.setOptions(genTable.getOptions());
        response.setRemark(genTable.getRemark());
        response.setCreateTime(genTable.getCreateTime());
        response.setUpdateTime(genTable.getUpdateTime());
        return response;
    }
}
