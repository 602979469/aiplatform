package com.jakt.aiplatform.web.checker;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.core.model.constant.FileConstants;
import com.jakt.aiplatform.web.param.FileInfoQueryRequest;
import com.jakt.aiplatform.web.param.FileInfoUpdateRequest;
import com.jakt.aiplatform.web.param.FileUploadRequest;

/**
 * 文件管理参数检查器：只做请求级校验（空值、长度、格式），不判断文件是否存在等业务问题。
 */
public final class FileInfoParamChecker {

    private FileInfoParamChecker() {
    }

    /**
     * 检查上传参数。
     *
     * @param request 上传请求
     */
    public static void checkUpload(FileUploadRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "上传参数不能为空");
        checkNamespace(request.getNamespace());
        AssertUtil.throwErrWhenNull(request.getFile(), ErrorCodeEnum.PARAM_INVALID, "上传文件不能为空");
        AssertUtil.throwErrWhenTrue(request.getFile().isEmpty(), ErrorCodeEnum.PARAM_INVALID, "上传文件不能为空");
        checkOriginalName(request.getFile().getOriginalFilename());
        AssertUtil.throwErrWhenTrue(StrUtil.length(request.getRemark()) > 500,
                ErrorCodeEnum.PARAM_INVALID, "备注长度不能超过 500");
    }

    /**
     * 检查分页查询参数。
     *
     * @param request 查询请求；可为 null（缺省分页，namespace 由 Manager 层校验）
     */
    public static void checkPage(FileInfoQueryRequest request) {
        if (ObjectUtil.isNull(request)) {
            return;
        }
        AssertUtil.throwErrWhenTrue(StrUtil.length(request.getFileName()) > 255,
                ErrorCodeEnum.PARAM_INVALID, "文件名长度不能超过 255");
    }

    /**
     * 检查更新元信息参数。
     *
     * @param request 更新请求
     */
    public static void checkUpdate(FileInfoUpdateRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "更新参数不能为空");
        checkNamespace(request.getNamespace());
        if (StrUtil.isNotBlank(request.getOriginalName())) {
            checkOriginalName(request.getOriginalName());
        }
        AssertUtil.throwErrWhenTrue(StrUtil.length(request.getRemark()) > 500,
                ErrorCodeEnum.PARAM_INVALID, "备注长度不能超过 500");
    }

    /**
     * 检查文件主键。
     *
     * @param id 文件主键
     */
    public static void checkId(Long id) {
        AssertUtil.throwErrWhenNull(id, ErrorCodeEnum.PARAM_INVALID, "文件ID不能为空");
    }

    /**
     * 检查业务命名空间：非空且符合格式（仅字母/数字/下划线/中划线）。
     *
     * @param namespace 业务命名空间
     */
    public static void checkNamespace(String namespace) {
        AssertUtil.throwErrWhenBlank(namespace, ErrorCodeEnum.PARAM_INVALID, "业务命名空间不能为空");
        AssertUtil.throwErrWhenFalse(ReUtil.isMatch(FileConstants.NAMESPACE_PATTERN, namespace),
                ErrorCodeEnum.PARAM_INVALID, "业务命名空间不合法（仅支持字母/数字/下划线/中划线，1-64位）");
    }

    /**
     * 检查原始文件名：非空、长度、禁止路径分隔符与控制字符。
     *
     * @param originalName 原始文件名
     */
    private static void checkOriginalName(String originalName) {
        AssertUtil.throwErrWhenBlank(originalName, ErrorCodeEnum.PARAM_INVALID, "文件名不能为空");
        AssertUtil.throwErrWhenTrue(originalName.length() > 255,
                ErrorCodeEnum.PARAM_INVALID, "文件名长度不能超过 255");
        AssertUtil.throwErrWhenTrue(originalName.contains("/") || originalName.contains("\\"),
                ErrorCodeEnum.PARAM_INVALID, "文件名不能包含路径分隔符");
        AssertUtil.throwErrWhenTrue(StrUtil.containsAny(originalName, '\u0000', '\n', '\r'),
                ErrorCodeEnum.PARAM_INVALID, "文件名包含非法字符");
    }
}
