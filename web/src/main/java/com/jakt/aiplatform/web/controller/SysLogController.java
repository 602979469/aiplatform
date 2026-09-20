package com.jakt.aiplatform.web.controller;

import cn.hutool.core.util.ObjectUtil;

import cn.dev33.satoken.annotation.SaIgnore;
import com.jakt.aiplatform.biz.service.SysLogManager;
import com.jakt.aiplatform.core.model.domain.SysLogFileDetail;
import com.jakt.aiplatform.core.model.domain.SysLogFileInfo;
import com.jakt.aiplatform.web.checker.SysLogParamChecker;
import com.jakt.aiplatform.web.param.SysLogDetailRequest;
import com.jakt.aiplatform.web.param.SysLogQueryRequest;
import com.jakt.aiplatform.web.result.ApiResult;
import com.jakt.aiplatform.web.template.ApiTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 系统日志。
 */
@RestController
@RequestMapping("/sys/log")
public class SysLogController {

    /** 系统日志 Manager。 */
    private final SysLogManager sysLogManager;

    public SysLogController(SysLogManager sysLogManager) {
        this.sysLogManager = sysLogManager;
    }

    /**
     * 查询日志列表（支持文件名模糊搜索和分页）。
     *
     * @param request 查询请求
     * @return 日志文件列表
     */
    @GetMapping("/list")
    @SaIgnore
    public ApiResult<List<SysLogFileInfo>> listLogFiles(SysLogQueryRequest request) {
        SysLogQueryRequest safeRequest = ObjectUtil.defaultIfNull(request, new SysLogQueryRequest());
        return ApiTemplate.execute(safeRequest, new ApiTemplate.Callback<SysLogQueryRequest,
                List<SysLogFileInfo>>() {
            @Override
            public void beforeService(SysLogQueryRequest param) {
                SysLogParamChecker.checkSysLogQueryRequest(param);
            }

            @Override
            public List<SysLogFileInfo> execute(SysLogQueryRequest param) {
                return sysLogManager.getLogFileList(param.getFileName(), param.getPageNum(), param.getPageSize());
            }
        });
    }

    /**
     * 查询日志详情（支持翻页和关键词搜索）。
     *
     * @param request 详情请求
     * @return 日志详情
     */
    @GetMapping("/detail")
    @SaIgnore
    public ApiResult<SysLogFileDetail> getLogDetail(SysLogDetailRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<SysLogDetailRequest,
                SysLogFileDetail>() {
            @Override
            public void beforeService(SysLogDetailRequest param) {
                SysLogParamChecker.checkSysLogDetailRequest(param);
            }

            @Override
            public SysLogFileDetail execute(SysLogDetailRequest param) {
                return sysLogManager.getLogDetail(param.getFileName(), param.getPageNum(),
                        param.getPageSize(), param.getKeyword());
            }
        });
    }
}
