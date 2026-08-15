package com.jakt.aiplatform.web.checker;

import com.jakt.aiplatform.common.util.tools.AssertUtil;
import com.jakt.aiplatform.common.util.tools.ParamValidator;
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
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "搜索参数不能为空");
        ParamValidator.validate(request);
    }

    /**
     * 检查下载生成参数。
     *
     * @param request 下载请求
     */
    public static void checkGenerate(MirrorDownloadRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "下载参数不能为空");
        ParamValidator.validate(request);
    }

    /**
     * 检查任务 ID。
     *
     * @param taskId 任务ID
     */
    public static void checkTaskId(String taskId) {
        AssertUtil.throwErrWhenBlank(taskId, ErrorCodeEnum.PARAM_INVALID, "任务ID不能为空");
    }

    /**
     * 检查文件名。
     *
     * @param fileName 文件名
     */
    public static void checkFileName(String fileName) {
        AssertUtil.throwErrWhenBlank(fileName, ErrorCodeEnum.PARAM_INVALID, "文件名不能为空");
    }
}
