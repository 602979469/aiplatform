package com.jakt.aiplatform.web.checker;

import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.common.util.tools.AiPlatformParamValidator;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.web.param.MirrorDownloadRequest;
import com.jakt.aiplatform.web.param.MirrorSearchRequest;

/**
 * 镜像加速器参数检查器。
 */
public class AiMirrorParamChecker {

    private AiMirrorParamChecker() {
    }

    /**
     * 检查镜像搜索参数。
     *
     * @param request 搜索请求
     */
    public static void checkSearch(MirrorSearchRequest request) {
        AiPlatformInvoker.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "搜索参数不能为空");
        AiPlatformParamValidator.validate(request);
    }

    /**
     * 检查下载生成参数。
     *
     * @param request 下载请求
     */
    public static void checkGenerate(MirrorDownloadRequest request) {
        AiPlatformInvoker.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "下载参数不能为空");
        AiPlatformParamValidator.validate(request);
    }

    /**
     * 检查任务 ID。
     *
     * @param taskId 任务ID
     */
    public static void checkTaskId(String taskId) {
        AiPlatformInvoker.throwErrWhenBlank(taskId, ErrorCodeEnum.PARAM_INVALID, "任务ID不能为空");
    }

    /**
     * 检查文件名。
     *
     * @param fileName 文件名
     */
    public static void checkFileName(String fileName) {
        AiPlatformInvoker.throwErrWhenBlank(fileName, ErrorCodeEnum.PARAM_INVALID, "文件名不能为空");
    }
}
