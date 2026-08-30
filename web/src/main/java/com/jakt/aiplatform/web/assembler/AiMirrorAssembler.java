package com.jakt.aiplatform.web.assembler;

import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.web.result.MirrorDownloadTask;
import com.jakt.aiplatform.web.result.MirrorImageResult;
import com.jakt.aiplatform.web.result.MirrorSearchResponse;

import java.util.List;

/**
 * 镜像加速器 DTO 组装器：领域 Model ↔ Response。
 */
public final class AiMirrorAssembler {

    private AiMirrorAssembler() {
    }

    /**
     * 搜索结果 Model → 响应。
     *
     * @param source 搜索结果模型
     * @return 搜索结果响应；入参为空返回 null
     */
    public static MirrorSearchResponse toSearchResponse(
            com.jakt.aiplatform.core.model.domain.MirrorSearchResponse source) {
        if (source == null) {
            return null;
        }
        List<MirrorImageResult> results = source.getResults() == null
                ? null
                : ConvertUtil.map(source.getResults(), AiMirrorAssembler::toImageResult);
        MirrorSearchResponse response = new MirrorSearchResponse();
        response.setOs(source.getOs());
        response.setArch(source.getArch());
        response.setResults(results);
        return response;
    }

    /**
     * 镜像结果 Model → 响应。
     *
     * @param source 镜像结果模型
     * @return 镜像结果响应；入参为空返回 null
     */
    public static MirrorImageResult toImageResult(com.jakt.aiplatform.core.model.domain.MirrorImageResult source) {
        if (source == null) {
            return null;
        }
        MirrorImageResult response = new MirrorImageResult();
        response.setVendor(source.getVendor());
        response.setRepo(source.getRepo());
        response.setTag(source.getTag());
        response.setFullName(source.getFullName());
        response.setArch(source.getArch());
        response.setLocalFileExists(source.isLocalFileExists());
        response.setLocalFileName(source.getLocalFileName());
        response.setFileId(source.getFileId());
        return response;
    }

    /**
     * 下载任务 Model → 响应。
     *
     * @param source 下载任务模型
     * @return 下载任务响应；入参为空返回 null
     */
    public static MirrorDownloadTask toDownloadTask(com.jakt.aiplatform.core.model.domain.MirrorDownloadTask source) {
        if (source == null) {
            return null;
        }
        MirrorDownloadTask response = new MirrorDownloadTask();
        response.setTaskId(source.getTaskId());
        response.setRepo(source.getRepo());
        response.setTag(source.getTag());
        response.setFileName(source.getFileName());
        response.setFileId(source.getFileId());
        response.setStatus(source.getStatus());
        response.setProgress(source.getProgress());
        response.setProgressMsg(source.getProgressMsg());
        response.setErrorCode(source.getErrorCode());
        response.setErrorMsg(source.getErrorMsg());
        response.setCreateTime(source.getCreateTime());
        response.setFinishTime(source.getFinishTime());
        return response;
    }
}
