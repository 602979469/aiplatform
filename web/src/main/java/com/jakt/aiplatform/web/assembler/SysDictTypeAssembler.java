package com.jakt.aiplatform.web.assembler;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.web.param.SysDictTypeCreateRequest;
import com.jakt.aiplatform.web.param.SysDictTypeQueryRequest;
import com.jakt.aiplatform.web.param.SysDictTypeUpdateRequest;
import com.jakt.aiplatform.web.result.SysDictTypeResponse;
import com.jakt.aiplatform.core.model.domain.SysDictType;
import com.jakt.aiplatform.core.model.param.SysDictTypeQueryParam;

/**
 * 字典类型对象组装器：DTO 与领域模型互转，只存在于 web。
 */
public final class SysDictTypeAssembler {

    private SysDictTypeAssembler() {
    }

    /**
     * 创建请求 DTO → 领域模型。
     *
     * @param request 创建字典类型请求 DTO
     * @return 字典类型领域模型
     */
    public static SysDictType toModel(SysDictTypeCreateRequest request) {
        SysDictType sysDictType = new SysDictType();
        sysDictType.setDictName(request.getDictName());
        sysDictType.setDictType(request.getDictType());
        sysDictType.setStatus(request.getStatus());
        sysDictType.setRemark(request.getRemark());
        return sysDictType;
    }

    /**
     * 更新请求 DTO + 路径 ID → 领域模型。
     *
     * @param request 更新字典类型请求 DTO
     * @param id      路径中的字典类型 ID
     * @return 字典类型领域模型
     */
    public static SysDictType toModel(SysDictTypeUpdateRequest request, Long id) {
        SysDictType sysDictType = new SysDictType();
        sysDictType.setDictId(id);
        sysDictType.setDictName(request.getDictName());
        sysDictType.setDictType(request.getDictType());
        sysDictType.setStatus(request.getStatus());
        sysDictType.setRemark(request.getRemark());
        return sysDictType;
    }

    /**
     * 查询请求 DTO → 查询参数。
     *
     * @param request 字典类型查询请求 DTO
     * @return 字典类型查询参数
     */
    public static SysDictTypeQueryParam toQueryParam(SysDictTypeQueryRequest request) {
        SysDictTypeQueryParam param = new SysDictTypeQueryParam();
        param.setDictId(request.getDictId());
        param.setDictName(request.getDictName());
        param.setDictType(request.getDictType());
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
     * @param sysDictType 字典类型领域模型
     * @return 字典类型响应 VO
     */
    public static SysDictTypeResponse toResponse(SysDictType sysDictType) {
        SysDictTypeResponse response = new SysDictTypeResponse();
        response.setDictId(sysDictType.getDictId());
        response.setDictName(sysDictType.getDictName());
        response.setDictType(sysDictType.getDictType());
        response.setStatus(sysDictType.getStatus());
        response.setRemark(sysDictType.getRemark());
        response.setCreateTime(sysDictType.getCreateTime());
        response.setUpdateTime(sysDictType.getUpdateTime());
        return response;
    }
}
