package com.jakt.aiplatform.web.assembler;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.web.param.SysDictDataCreateRequest;
import com.jakt.aiplatform.web.param.SysDictDataQueryRequest;
import com.jakt.aiplatform.web.param.SysDictDataUpdateRequest;
import com.jakt.aiplatform.web.result.SysDictDataResponse;
import com.jakt.aiplatform.core.model.domain.SysDictData;
import com.jakt.aiplatform.core.model.param.SysDictDataQueryParam;

/**
 * 字典数据对象组装器：DTO 与领域模型互转，只存在于 web。
 */
public final class SysDictDataAssembler {

    private SysDictDataAssembler() {
    }

    /**
     * 创建请求 DTO → 领域模型。
     *
     * @param request 创建字典数据请求 DTO
     * @return 字典数据领域模型
     */
    public static SysDictData toModel(SysDictDataCreateRequest request) {
        SysDictData sysDictData = new SysDictData();
        sysDictData.setDictSort(request.getDictSort());
        sysDictData.setDictLabel(request.getDictLabel());
        sysDictData.setDictValue(request.getDictValue());
        sysDictData.setDictType(request.getDictType());
        sysDictData.setCssClass(request.getCssClass());
        sysDictData.setListClass(request.getListClass());
        sysDictData.setIsDefault(request.getIsDefault());
        sysDictData.setStatus(request.getStatus());
        sysDictData.setRemark(request.getRemark());
        return sysDictData;
    }

    /**
     * 更新请求 DTO + 路径 ID → 领域模型。
     *
     * @param request 更新字典数据请求 DTO
     * @param id      路径中的字典数据 ID
     * @return 字典数据领域模型
     */
    public static SysDictData toModel(SysDictDataUpdateRequest request, Long id) {
        SysDictData sysDictData = new SysDictData();
        sysDictData.setDictCode(id);
        sysDictData.setDictSort(request.getDictSort());
        sysDictData.setDictLabel(request.getDictLabel());
        sysDictData.setDictValue(request.getDictValue());
        sysDictData.setDictType(request.getDictType());
        sysDictData.setCssClass(request.getCssClass());
        sysDictData.setListClass(request.getListClass());
        sysDictData.setIsDefault(request.getIsDefault());
        sysDictData.setStatus(request.getStatus());
        sysDictData.setRemark(request.getRemark());
        return sysDictData;
    }

    /**
     * 查询请求 DTO → 查询参数。
     *
     * @param request 字典数据查询请求 DTO
     * @return 字典数据查询参数
     */
    public static SysDictDataQueryParam toQueryParam(SysDictDataQueryRequest request) {
        SysDictDataQueryParam param = new SysDictDataQueryParam();
        param.setDictCode(request.getDictCode());
        param.setDictSort(request.getDictSort());
        param.setDictLabel(request.getDictLabel());
        param.setDictValue(request.getDictValue());
        param.setDictType(request.getDictType());
        param.setCssClass(request.getCssClass());
        param.setListClass(request.getListClass());
        param.setIsDefault(request.getIsDefault());
        param.setStatus(request.getStatus());
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
     * @param sysDictData 字典数据领域模型
     * @return 字典数据响应 VO
     */
    public static SysDictDataResponse toResponse(SysDictData sysDictData) {
        SysDictDataResponse response = new SysDictDataResponse();
        response.setDictCode(sysDictData.getDictCode());
        response.setDictSort(sysDictData.getDictSort());
        response.setDictLabel(sysDictData.getDictLabel());
        response.setDictValue(sysDictData.getDictValue());
        response.setDictType(sysDictData.getDictType());
        response.setCssClass(sysDictData.getCssClass());
        response.setListClass(sysDictData.getListClass());
        response.setIsDefault(sysDictData.getIsDefault());
        response.setStatus(sysDictData.getStatus());
        response.setRemark(sysDictData.getRemark());
        response.setCreateTime(sysDictData.getCreateTime());
        response.setUpdateTime(sysDictData.getUpdateTime());
        return response;
    }
}
