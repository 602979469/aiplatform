package com.jakt.aiplatform.web.assembler;

import cn.hutool.core.collection.CollUtil;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.core.model.domain.CdcEsMapping;
import com.jakt.aiplatform.core.model.domain.CdcMappingCheck;
import com.jakt.aiplatform.core.model.domain.CdcMappingPreview;
import com.jakt.aiplatform.core.model.domain.CdcSyncStatus;
import com.jakt.aiplatform.web.param.CdcMappingSaveRequest;
import com.jakt.aiplatform.web.result.CdcMappingPreviewResponse;
import com.jakt.aiplatform.web.result.CdcMappingResponse;
import com.jakt.aiplatform.web.result.CdcSyncStatusResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * ES 同步配置装配器。
 */
public final class CdcSyncAssembler {

    private CdcSyncAssembler() {
    }

    /**
     * 保存请求转领域对象。
     *
     * @param request 保存请求
     * @return 领域对象；入参为空返回 null
     */
    public static CdcEsMapping toMapping(CdcMappingSaveRequest request) {
        if (request == null) {
            return null;
        }
        CdcEsMapping mapping = new CdcEsMapping();
        mapping.setName(request.getName());
        mapping.setEsIndex(request.getEsIndex());
        mapping.setSql(request.getSql());
        mapping.setPk(request.getPk());
        mapping.setUpsert(request.getUpsert());
        mapping.setCommitBatch(request.getCommitBatch());
        return mapping;
    }

    /**
     * 领域对象转响应。
     *
     * @param mapping 领域对象
     * @return 响应；入参为空返回 null
     */
    public static CdcMappingResponse toResponse(CdcEsMapping mapping) {
        if (mapping == null) {
            return null;
        }
        CdcMappingResponse response = new CdcMappingResponse();
        response.setName(mapping.getName());
        response.setEsIndex(mapping.getEsIndex());
        response.setSql(mapping.getSql());
        response.setPk(mapping.getPk());
        response.setUpsert(mapping.getUpsert());
        response.setCommitBatch(mapping.getCommitBatch());
        response.setTableName(mapping.getTableName());
        response.setRawYml(mapping.getRawYml());
        return response;
    }

    /**
     * 映射列表转响应列表。
     *
     * @param mappings 领域对象列表
     * @return 响应列表
     */
    public static List<CdcMappingResponse> toResponseList(List<CdcEsMapping> mappings) {
        return ConvertUtil.map(mappings, CdcSyncAssembler::toResponse);
    }

    /**
     * 预检结果转响应。
     *
     * @param preview 预检结果
     * @return 响应；入参为空返回 null
     */
    public static CdcMappingPreviewResponse toPreviewResponse(CdcMappingPreview preview) {
        if (preview == null) {
            return null;
        }
        CdcMappingPreviewResponse response = new CdcMappingPreviewResponse();
        response.setPass(preview.getPass());
        response.setYml(preview.getYml());
        List<CdcMappingPreviewResponse.Check> checks = new ArrayList<>();
        if (CollUtil.isNotEmpty(preview.getChecks())) {
            for (CdcMappingCheck check : preview.getChecks()) {
                CdcMappingPreviewResponse.Check item = new CdcMappingPreviewResponse.Check();
                item.setItem(check.getItem());
                item.setLevel(check.getLevel());
                item.setPass(check.getPassed());
                item.setMessage(check.getMessage());
                checks.add(item);
            }
        }
        response.setChecks(checks);
        return response;
    }

    /**
     * 运行状态转响应。
     *
     * @param status 运行状态
     * @return 响应；入参为空返回 null
     */
    public static CdcSyncStatusResponse toStatusResponse(CdcSyncStatus status) {
        if (status == null) {
            return null;
        }
        CdcSyncStatusResponse response = new CdcSyncStatusResponse();
        response.setReadyReplicas(status.getReadyReplicas());
        response.setReplicas(status.getReplicas());
        response.setDestinations(status.getDestinations());
        return response;
    }
}
