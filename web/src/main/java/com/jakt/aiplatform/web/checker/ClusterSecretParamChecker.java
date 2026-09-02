package com.jakt.aiplatform.web.checker;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.common.framework.error.CommonException;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.framework.tools.ParamValidator;
import com.jakt.aiplatform.common.util.error.CommonErrorCode;
import com.jakt.aiplatform.web.param.ClusterSecretQueryRequest;
import com.jakt.aiplatform.web.param.ClusterSecretUpsertRequest;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 密钥参数检查器。
 */
public final class ClusterSecretParamChecker {

    /** DNS-1123 子域名：小写字母/数字/中划线/点。 */
    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[a-z0-9]([-a-z0-9]*[a-z0-9])?(\\.[a-z0-9]([-a-z0-9]*[a-z0-9])?)*$");

    /** Secret 键名：字母/数字/点/中划线/下划线，不能以点开头或结尾。 */
    private static final Pattern KEY_PATTERN = Pattern.compile("^[A-Za-z0-9._-]+$");

    private ClusterSecretParamChecker() {
    }

    public static void checkPageRequest(ClusterSecretQueryRequest request) {
        if (request != null) {
            ParamValidator.validate(request);
            if (StrUtil.isNotBlank(request.getNamespace())) {
                AssertUtil.throwErrWhenFalse(NAME_PATTERN.matcher(request.getNamespace()).matches(),
                        CommonErrorCode.PARAM_INVALID, "命名空间格式不合法");
            }
        }
    }

    public static void checkDetail(String namespace, String name) {
        checkNamespaceName(namespace, name);
    }

    public static void checkUpsertRequest(ClusterSecretUpsertRequest request) {
        AssertUtil.throwErrWhenNull(request, CommonErrorCode.PARAM_INVALID, "密钥参数不能为空");
        ParamValidator.validate(request);
        checkNamespaceName(request.getNamespace(), request.getName());
        if (CollUtil.isEmpty(request.getKeys())) {
            throw CommonException.of(CommonErrorCode.PARAM_INVALID, "至少提交一个键值");
        }
        Set<String> seen = new HashSet<>();
        for (ClusterSecretUpsertRequest.Item item : request.getKeys()) {
            if (StrUtil.isBlank(item.getKey()) || !KEY_PATTERN.matcher(item.getKey()).matches()
                || item.getKey().startsWith(".") || item.getKey().endsWith(".")
                || ".".equals(item.getKey()) || "..".equals(item.getKey())) {
                throw CommonException.of(CommonErrorCode.PARAM_INVALID, "键名不合法: " + item.getKey());
            }
            if (!seen.add(item.getKey())) {
                throw CommonException.of(CommonErrorCode.PARAM_INVALID, "重复的键名: " + item.getKey());
            }
            if (StrUtil.isBlank(item.getValue())) {
                throw CommonException.of(CommonErrorCode.PARAM_INVALID, "键值不能为空: " + item.getKey());
            }
        }
    }

    private static void checkNamespaceName(String namespace, String name) {
        AssertUtil.throwErrWhenBlank(namespace, CommonErrorCode.PARAM_INVALID, "命名空间不能为空");
        AssertUtil.throwErrWhenFalse(NAME_PATTERN.matcher(namespace).matches(),
                CommonErrorCode.PARAM_INVALID, "命名空间格式不合法");
        AssertUtil.throwErrWhenBlank(name, CommonErrorCode.PARAM_INVALID, "Secret 名称不能为空");
        AssertUtil.throwErrWhenFalse(NAME_PATTERN.matcher(name).matches(),
                CommonErrorCode.PARAM_INVALID, "Secret 名称格式不合法（DNS-1123）");
    }

}
