package com.jakt.aiplatform.web.checker;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.framework.error.CommonException;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.framework.tools.ParamValidator;
import com.jakt.aiplatform.web.param.ClusterPodConfigCreateRequest;
import com.jakt.aiplatform.web.param.ClusterPodConfigQueryRequest;
import com.jakt.aiplatform.web.param.ClusterPodConfigUpdateRequest;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

/**
 * 业务pod配置表参数检查器。
 */
public final class ClusterPodConfigParamChecker {

    private ClusterPodConfigParamChecker() {
    }

    /**
     * 检查业务pod配置表创建参数。
     *
     * @param request 业务pod配置表创建请求
     */
    public static void checkClusterPodConfigCreateRequest(ClusterPodConfigCreateRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "创建参数不能为空");
        ParamValidator.validate(request);
        checkDeployYaml(request.getDeployYaml());
    }

    /**
     * 检查业务pod配置表更新参数。
     *
     * @param request 业务pod配置表更新请求
     */
    public static void checkClusterPodConfigUpdateRequest(ClusterPodConfigUpdateRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "更新参数不能为空");
        ParamValidator.validate(request);
        checkDeployYaml(request.getDeployYaml());
    }

    /**
     * 检查业务pod配置表主键参数（按主键查询/更新/删除共用）。
     *
     * @param id 业务pod配置表主键
     */
    public static void checkId(Long id) {
        AssertUtil.throwErrWhenNull(id, ErrorCodeEnum.PARAM_INVALID, "业务pod配置表ID不能为空");
    }

    /**
     * 检查业务pod配置表查询参数。
     *
     * @param request 业务pod配置表查询请求，可为 null（缺省分页）
     */
    public static void checkClusterPodConfigQueryRequest(ClusterPodConfigQueryRequest request) {
        if (ObjectUtil.isNull(request)) {
            return;
        }
        ParamValidator.validate(request);
    }

    /**
     * 检查 pod 名称（日志/事件查询用）。
     *
     * @param podName pod 名称
     */
    public static void checkPodName(String podName) {
        AssertUtil.throwErrWhenBlank(podName, ErrorCodeEnum.PARAM_INVALID, "pod名称不能为空");
        AssertUtil.throwErrWhenTrue(podName.length() > 64, ErrorCodeEnum.PARAM_INVALID, "pod名称长度不能超过 64");
    }

    /**
     * 校验 Deployment YAML 格式：仅要求可被 YAML 解析，不做内容/越权检查。
     *
     * @param deployYaml Deployment YAML
     */
    private static void checkDeployYaml(String deployYaml) {
        AssertUtil.throwErrWhenBlank(deployYaml, ErrorCodeEnum.PARAM_INVALID, "Deployment YAML不能为空");
        try {
            LoaderOptions loaderOptions = new LoaderOptions();
            loaderOptions.setAllowDuplicateKeys(false);
            new Yaml(loaderOptions).load(deployYaml);
        } catch (YAMLException e) {
            throw new CommonException(ErrorCodeEnum.PARAM_INVALID.getCode(),
                    "Deployment YAML 格式不合法: " + e.getMessage());
        }
    }
}
