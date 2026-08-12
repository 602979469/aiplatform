package com.jakt.aiplatform.web.assembler;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.web.param.GenTableColumnCreateRequest;
import com.jakt.aiplatform.web.param.GenTableColumnQueryRequest;
import com.jakt.aiplatform.web.param.GenTableColumnUpdateRequest;
import com.jakt.aiplatform.web.result.GenTableColumnResponse;
import com.jakt.aiplatform.core.model.domain.GenTableColumn;
import com.jakt.aiplatform.core.model.param.GenTableColumnQueryParam;

/**
 * 代码生成字段对象组装器：DTO 与领域模型互转，只存在于 web。
 */
public final class GenTableColumnAssembler {

    private GenTableColumnAssembler() {
    }

    /**
     * 创建请求 DTO → 领域模型。
     *
     * @param request 创建代码生成字段请求 DTO
     * @return 代码生成字段领域模型
     */
    public static GenTableColumn toModel(GenTableColumnCreateRequest request) {
        GenTableColumn genTableColumn = new GenTableColumn();
        genTableColumn.setTableId(request.getTableId());
        genTableColumn.setColumnName(request.getColumnName());
        genTableColumn.setColumnComment(request.getColumnComment());
        genTableColumn.setColumnType(request.getColumnType());
        genTableColumn.setJavaType(request.getJavaType());
        genTableColumn.setJavaField(request.getJavaField());
        genTableColumn.setIsPk(request.getIsPk());
        genTableColumn.setIsIncrement(request.getIsIncrement());
        genTableColumn.setIsRequired(request.getIsRequired());
        genTableColumn.setIsInsert(request.getIsInsert());
        genTableColumn.setIsEdit(request.getIsEdit());
        genTableColumn.setIsList(request.getIsList());
        genTableColumn.setIsQuery(request.getIsQuery());
        genTableColumn.setQueryType(request.getQueryType());
        genTableColumn.setHtmlType(request.getHtmlType());
        genTableColumn.setDictType(request.getDictType());
        genTableColumn.setSort(request.getSort());
        return genTableColumn;
    }

    /**
     * 更新请求 DTO + 路径 ID → 领域模型。
     *
     * @param request 更新代码生成字段请求 DTO
     * @param id      路径中的代码生成字段 ID
     * @return 代码生成字段领域模型
     */
    public static GenTableColumn toModel(GenTableColumnUpdateRequest request, Long id) {
        GenTableColumn genTableColumn = new GenTableColumn();
        genTableColumn.setColumnId(id);
        genTableColumn.setTableId(request.getTableId());
        genTableColumn.setColumnName(request.getColumnName());
        genTableColumn.setColumnComment(request.getColumnComment());
        genTableColumn.setColumnType(request.getColumnType());
        genTableColumn.setJavaType(request.getJavaType());
        genTableColumn.setJavaField(request.getJavaField());
        genTableColumn.setIsPk(request.getIsPk());
        genTableColumn.setIsIncrement(request.getIsIncrement());
        genTableColumn.setIsRequired(request.getIsRequired());
        genTableColumn.setIsInsert(request.getIsInsert());
        genTableColumn.setIsEdit(request.getIsEdit());
        genTableColumn.setIsList(request.getIsList());
        genTableColumn.setIsQuery(request.getIsQuery());
        genTableColumn.setQueryType(request.getQueryType());
        genTableColumn.setHtmlType(request.getHtmlType());
        genTableColumn.setDictType(request.getDictType());
        genTableColumn.setSort(request.getSort());
        return genTableColumn;
    }

    /**
     * 查询请求 DTO → 查询参数。
     *
     * @param request 代码生成字段查询请求 DTO
     * @return 代码生成字段查询参数
     */
    public static GenTableColumnQueryParam toQueryParam(GenTableColumnQueryRequest request) {
        GenTableColumnQueryParam param = new GenTableColumnQueryParam();
        param.setColumnId(request.getColumnId());
        param.setTableId(request.getTableId());
        param.setColumnName(request.getColumnName());
        param.setColumnComment(request.getColumnComment());
        param.setColumnType(request.getColumnType());
        param.setJavaType(request.getJavaType());
        param.setJavaField(request.getJavaField());
        param.setIsPk(request.getIsPk());
        param.setIsIncrement(request.getIsIncrement());
        param.setIsRequired(request.getIsRequired());
        param.setIsInsert(request.getIsInsert());
        param.setIsEdit(request.getIsEdit());
        param.setIsList(request.getIsList());
        param.setIsQuery(request.getIsQuery());
        param.setQueryType(request.getQueryType());
        param.setHtmlType(request.getHtmlType());
        param.setDictType(request.getDictType());
        param.setSort(request.getSort());
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
     * @param genTableColumn 代码生成字段领域模型
     * @return 代码生成字段响应 VO
     */
    public static GenTableColumnResponse toResponse(GenTableColumn genTableColumn) {
        GenTableColumnResponse response = new GenTableColumnResponse();
        response.setColumnId(genTableColumn.getColumnId());
        response.setTableId(genTableColumn.getTableId());
        response.setColumnName(genTableColumn.getColumnName());
        response.setColumnComment(genTableColumn.getColumnComment());
        response.setColumnType(genTableColumn.getColumnType());
        response.setJavaType(genTableColumn.getJavaType());
        response.setJavaField(genTableColumn.getJavaField());
        response.setIsPk(genTableColumn.getIsPk());
        response.setIsIncrement(genTableColumn.getIsIncrement());
        response.setIsRequired(genTableColumn.getIsRequired());
        response.setIsInsert(genTableColumn.getIsInsert());
        response.setIsEdit(genTableColumn.getIsEdit());
        response.setIsList(genTableColumn.getIsList());
        response.setIsQuery(genTableColumn.getIsQuery());
        response.setQueryType(genTableColumn.getQueryType());
        response.setHtmlType(genTableColumn.getHtmlType());
        response.setDictType(genTableColumn.getDictType());
        response.setSort(genTableColumn.getSort());
        response.setCreateTime(genTableColumn.getCreateTime());
        response.setUpdateTime(genTableColumn.getUpdateTime());
        return response;
    }
}
