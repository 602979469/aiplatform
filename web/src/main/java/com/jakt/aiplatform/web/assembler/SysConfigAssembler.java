package com.jakt.aiplatform.web.assembler;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.web.param.SysConfigCreateRequest;
import com.jakt.aiplatform.web.param.SysConfigQueryRequest;
import com.jakt.aiplatform.web.param.SysConfigUpdateRequest;
import com.jakt.aiplatform.web.result.SysConfigResponse;
import com.jakt.aiplatform.core.model.domain.SysConfig;
import com.jakt.aiplatform.core.model.param.SysConfigQueryParam;

/**
 * 参数配置对象组装器：DTO 与领域模型互转，只存在于 web。
 */
public final class SysConfigAssembler {

    private SysConfigAssembler() {
    }

    /**
     * 创建请求 DTO → 领域模型。
     *
     * @param request 创建参数配置请求 DTO
     * @return 参数配置领域模型
     */
    public static SysConfig toModel(SysConfigCreateRequest request) {
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigName(request.getConfigName());
        sysConfig.setConfigKey(request.getConfigKey());
        sysConfig.setConfigValue(request.getConfigValue());
        sysConfig.setConfigType(request.getConfigType());
        sysConfig.setRemark(request.getRemark());
        return sysConfig;
    }

    /**
     * 更新请求 DTO + 路径 ID → 领域模型。
     *
     * @param request 更新参数配置请求 DTO
     * @param id      路径中的参数配置 ID
     * @return 参数配置领域模型
     */
    public static SysConfig toModel(SysConfigUpdateRequest request, Long id) {
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigId(id);
        sysConfig.setConfigName(request.getConfigName());
        sysConfig.setConfigKey(request.getConfigKey());
        sysConfig.setConfigValue(request.getConfigValue());
        sysConfig.setConfigType(request.getConfigType());
        sysConfig.setRemark(request.getRemark());
        return sysConfig;
    }

    /**
     * 查询请求 DTO → 查询参数。
     *
     * @param request 参数配置查询请求 DTO
     * @return 参数配置查询参数
     */
    public static SysConfigQueryParam toQueryParam(SysConfigQueryRequest request) {
        SysConfigQueryParam param = new SysConfigQueryParam();
        param.setConfigId(request.getConfigId());
        param.setConfigName(request.getConfigName());
        param.setConfigKey(request.getConfigKey());
        param.setConfigValue(request.getConfigValue());
        param.setConfigType(request.getConfigType());
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
     * @param sysConfig 参数配置领域模型
     * @return 参数配置响应 VO
     */
    public static SysConfigResponse toResponse(SysConfig sysConfig) {
        SysConfigResponse response = new SysConfigResponse();
        response.setConfigId(sysConfig.getConfigId());
        response.setConfigName(sysConfig.getConfigName());
        response.setConfigKey(sysConfig.getConfigKey());
        response.setConfigValue(sysConfig.getConfigValue());
        response.setConfigType(sysConfig.getConfigType());
        response.setRemark(sysConfig.getRemark());
        response.setCreateTime(sysConfig.getCreateTime());
        response.setUpdateTime(sysConfig.getUpdateTime());
        return response;
    }
}
