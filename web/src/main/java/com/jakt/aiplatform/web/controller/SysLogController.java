package com.jakt.aiplatform.web.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.jakt.aiplatform.biz.service.SysLogManager;
import com.jakt.aiplatform.common.framework.error.CommonException;
import com.jakt.aiplatform.common.util.error.CommonErrorCode;
import com.jakt.aiplatform.core.model.domain.SysLogFileDetail;
import com.jakt.aiplatform.core.model.domain.SysLogFileInfo;
import com.jakt.aiplatform.web.checker.SysLogParamChecker;
import com.jakt.aiplatform.web.param.SysLogDetailRequest;
import com.jakt.aiplatform.web.param.SysLogQueryRequest;
import com.jakt.aiplatform.web.result.ApiResult;
import jakarta.validation.ValidationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 系统日志。
 *
 * <p>本控制器刻意不走 {@code ApiTemplate}：查看日志本身不应产生日志（否则越看越多，污染日志文件）。
 * 因此直接调用 manager，自行做参数校验与异常封装，全程无 biz-service / common-digest / common-error 输出。
 */
@RestController
@RequestMapping("/sys/log")
public class SysLogController {

    private final SysLogManager sysLogManager;

    public SysLogController(SysLogManager sysLogManager) {
        this.sysLogManager = sysLogManager;
    }

    /**
     * 查询日志列表（支持文件名模糊搜索和分页），无日志输出。
     *
     * @param request 查询请求
     * @return 日志文件列表
     */
    @GetMapping("/list")
    @SaIgnore
    public ApiResult<List<SysLogFileInfo>> listLogFiles(SysLogQueryRequest request) {
        try {
            SysLogParamChecker.checkSysLogQueryRequest(request);
            if (request == null) {
                request = new SysLogQueryRequest();
            }
            List<SysLogFileInfo> list = sysLogManager.getLogFileList(
                    request.getFileName(), request.getPageNum(), request.getPageSize());
            return ApiResult.ok(list);
        } catch (CommonException e) {
            return ApiResult.fail(e.getErrorCode(), e.getErrorMessage());
        } catch (ValidationException e) {
            return ApiResult.fail(CommonErrorCode.PARAM_INVALID, e.getMessage());
        } catch (Exception e) {
            return ApiResult.fail(CommonErrorCode.SYSTEM_ERROR, e.getMessage());
        }
    }

    /**
     * 查询日志详情（支持翻页和关键词搜索），无日志输出。
     *
     * @param request 详情请求
     * @return 日志详情
     */
    @GetMapping("/detail")
    @SaIgnore
    public ApiResult<SysLogFileDetail> getLogDetail(SysLogDetailRequest request) {
        try {
            SysLogParamChecker.checkSysLogDetailRequest(request);
            SysLogFileDetail detail = sysLogManager.getLogDetail(
                    request.getFileName(), request.getPageNum(), request.getPageSize(), request.getKeyword());
            return ApiResult.ok(detail);
        } catch (CommonException e) {
            return ApiResult.fail(e.getErrorCode(), e.getErrorMessage());
        } catch (ValidationException e) {
            return ApiResult.fail(CommonErrorCode.PARAM_INVALID, e.getMessage());
        } catch (Exception e) {
            return ApiResult.fail(CommonErrorCode.SYSTEM_ERROR, e.getMessage());
        }
    }
}
