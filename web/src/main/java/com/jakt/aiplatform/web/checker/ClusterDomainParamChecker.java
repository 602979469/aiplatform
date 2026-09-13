package com.jakt.aiplatform.web.checker;

import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.framework.tools.ParamValidator;
import com.jakt.aiplatform.common.util.error.CommonErrorCode;
import com.jakt.aiplatform.web.param.ClusterDomainDisableRequest;
import com.jakt.aiplatform.web.param.ClusterDomainDeleteRequest;
import com.jakt.aiplatform.web.param.ClusterDomainEnableRequest;

import java.util.regex.Pattern;

/**
 * 域名映射参数检查器：域名限定 *.jakt.online（与脚本侧校验保持一致，防注入）。
 */
public final class ClusterDomainParamChecker {

    /** 只允许 xxxx.jakt.online。 */
    private static final Pattern DOMAIN_PATTERN =
            Pattern.compile("^([a-z0-9]([a-z0-9-]*[a-z0-9])?\\.)+jakt\\.online$");

    /** 反代上游：host:port（如 127.0.0.1:8080）。 */
    private static final Pattern UPSTREAM_PATTERN =
            Pattern.compile("^[A-Za-z0-9._-]+:[0-9]{1,5}$");

    private ClusterDomainParamChecker() {
    }

    public static void checkEnableRequest(ClusterDomainEnableRequest request) {
        AssertUtil.throwErrWhenNull(request, CommonErrorCode.PARAM_INVALID, "参数不能为空");
        ParamValidator.validate(request);
        checkDomain(request.getDomain());
        if (StrUtil.isNotBlank(request.getUpstream())) {
            AssertUtil.throwErrWhenFalse(UPSTREAM_PATTERN.matcher(request.getUpstream()).matches(),
                    CommonErrorCode.PARAM_INVALID, "上游地址格式不合法（形如 127.0.0.1:8080）");
        }
    }

    public static void checkDisableRequest(ClusterDomainDisableRequest request) {
        AssertUtil.throwErrWhenNull(request, CommonErrorCode.PARAM_INVALID, "参数不能为空");
        ParamValidator.validate(request);
        checkDomain(request.getDomain());
    }

    public static void checkDeleteRequest(ClusterDomainDeleteRequest request) {
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
