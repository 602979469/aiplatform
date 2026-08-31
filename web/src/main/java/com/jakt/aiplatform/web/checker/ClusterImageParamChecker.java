package com.jakt.aiplatform.web.checker;

import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.framework.tools.ParamValidator;
import com.jakt.aiplatform.common.util.error.CommonErrorCode;
import com.jakt.aiplatform.core.model.enums.ClusterImageTypeEnum;
import com.jakt.aiplatform.web.param.ClusterImageCreateRequest;
import com.jakt.aiplatform.web.param.ClusterImageQueryRequest;
import com.jakt.aiplatform.web.param.ClusterImageUpdateRequest;

/**
 * 镜像参数检查器。
 */
public final class ClusterImageParamChecker {

    private ClusterImageParamChecker() {
    }

    public static void checkId(Long id) {
        AssertUtil.throwErrWhenNull(id, CommonErrorCode.PARAM_INVALID, "镜像ID不能为空");
    }

    public static void checkCreateRequest(ClusterImageCreateRequest request) {
        AssertUtil.throwErrWhenNull(request, CommonErrorCode.PARAM_INVALID, "镜像参数不能为空");
        ParamValidator.validate(request);
        checkTypeFields(request.getImageType(), request.getGitUrl(), request.getGitBranch(),
                request.getDockerfile(), request.getExternalImage());
    }

    public static void checkUpdateRequest(ClusterImageUpdateRequest request) {
        AssertUtil.throwErrWhenNull(request, CommonErrorCode.PARAM_INVALID, "镜像参数不能为空");
        ParamValidator.validate(request);
        checkTypeFields(request.getImageType(), request.getGitUrl(), request.getGitBranch(),
                request.getDockerfile(), request.getExternalImage());
    }

    public static void checkQueryRequest(ClusterImageQueryRequest request) {
        if (request != null) {
            ParamValidator.validate(request);
        }
    }

    private static void checkTypeFields(ClusterImageTypeEnum type, String gitUrl, String gitBranch,
                                        String dockerfile, String externalImage) {
        if (type == ClusterImageTypeEnum.BUILD) {
            AssertUtil.throwErrWhenBlank(gitUrl, CommonErrorCode.PARAM_INVALID, "自研镜像必须提供 git 地址");
            AssertUtil.throwErrWhenBlank(gitBranch, CommonErrorCode.PARAM_INVALID, "自研镜像必须提供 git 分支");
            AssertUtil.throwErrWhenBlank(dockerfile, CommonErrorCode.PARAM_INVALID, "自研镜像必须提供 Dockerfile");
        } else if (type == ClusterImageTypeEnum.EXTERNAL) {
            AssertUtil.throwErrWhenBlank(externalImage, CommonErrorCode.PARAM_INVALID, "现成镜像必须提供外部镜像地址");
        }
    }
}
