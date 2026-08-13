package com.jakt.aiplatform.web.assembler;

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
                : source.getResults().stream().map(AiMirrorAssembler::toImageResult).toList();
        return MirrorSearchResponse.builder()
                .os(source.getOs())
                .arch(source.getArch())
                .results(results)
                .build();
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
        return MirrorImageResult.builder()
                .vendor(source.getVendor())
                .repo(source.getRepo())
                .tag(source.getTag())
                .fullName(source.getFullName())
                .arch(source.getArch())
                .localFileExists(source.isLocalFileExists())
                .localFileName(source.getLocalFileName())
                .build();
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
        return MirrorDownloadTask.builder()
                .taskId(source.getTaskId())
                .repo(source.getRepo())
                .tag(source.getTag())
                .fileName(source.getFileName())
                .status(source.getStatus())
                .progress(source.getProgress())
                .progressMsg(source.getProgressMsg())
                .errorCode(source.getErrorCode())
                .errorMsg(source.getErrorMsg())
                .createTime(source.getCreateTime())
                .finishTime(source.getFinishTime())
                .build();
    }
}
