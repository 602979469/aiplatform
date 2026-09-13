package com.jakt.aiplatform.web.checker;

import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.framework.tools.ParamValidator;
import com.jakt.aiplatform.common.util.error.CommonErrorCode;
import com.jakt.aiplatform.web.param.ClusterDomainAddRequest;
import com.jakt.aiplatform.web.param.ClusterDomainRemoveRequest;

import java.util.regex.Pattern;

/**
 * 域名映射参数检查器：域名限定 *.jakt.online（与脚本侧校验保持一致，防注入）。
 */
public final class ClusterDomainParamChecker {

    /** 只允许 xxxx.jakt.online。 */
    private static final Pattern DOMAIN_PATTERN =
            Pattern.compile("^[a-z0-9]([a-z0-9-]*[a-z0-9])?\\.jakt\\.online$");

    /** DNS-1123 名称（命名空间/Service）。 */
    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[a-z0-9]([-a-z0-9]*[a-z0-9])?$");

    private ClusterDomainParamChecker() {
    }

    public static void checkAddRequest(ClusterDomainAddRequest request) {
        AssertUtil.throwErrWhenNull(request, CommonErrorCode.PARAM_INVALID, "参数不能为空");
        ParamValidator.validate(request);
        checkDomain(request.getDomain());
        if (StrUtil.isNotBlank(request.getNamespace()) || StrUtil.isNotBlank(request.getService())) {
            AssertUtil.throwErrWhenFalse(
                    StrUtil.isNotBlank(request.getNamespace()) && StrUtil.isNotBlank(request.getService()),
                    CommonErrorCode.PARAM_INVALID, "命名空间与 Service 需同时填写（或都留空，仅加公网入口）");
            AssertUtil.throwErrWhenFalse(NAME_PATTERN.matcher(request.getNamespace()).matches(),
                    CommonErrorCode.PARAM_INVALID, "命名空间格式不合法");
            AssertUtil.throwErrWhenFalse(NAME_PATTERN.matcher(request.getService()).matches(),
                    CommonErrorCode.PARAM_INVALID, "Service 名称格式不合法");
            int port = request.getPort() == null ? 80 : request.getPort();
            AssertUtil.throwErrWhenFalse(port >= 1 && port <= 65535,
                    CommonErrorCode.PARAM_INVALID, "端口必须在 1-65535 之间");
        }
    }

    public static void checkRemoveRequest(ClusterDomainRemoveRequest request) {
        AssertUtil.throwErrWhenNull(request, CommonErrorCode.PARAM_INVALID, "参数不能为空");
        ParamValidator.validate(request);
        checkDomain(request.getDomain());
    }

    /**
     * 域名格式校验。
     *
     * @param domain 域名
     */
    private static void checkDomain(String domain) {
        AssertUtil.throwErrWhenFalse(
                StrUtil.isNotBlank(domain) && DOMAIN_PATTERN.matcher(domain).matches(),
                CommonErrorCode.PARAM_INVALID, "域名只允许 xxxx.jakt.online 形式");
    }
}
