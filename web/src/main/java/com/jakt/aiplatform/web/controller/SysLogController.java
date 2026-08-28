package com.jakt.aiplatform.web.controller;

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
 * 系统日志
 */
@RestController
@RequestMapping("/sys/log")
public class SysLogController {

    private final SysLogManager sysLogManager;

    public SysLogController(SysLogManager sysLogManager) {
        this.sysLogManager = sysLogManager;
    }

    /**
     * 查询日志列表（支持文件名模糊搜索和分页）
     */
    @GetMapping("/list")
    @SaIgnore
    public ApiResult<List<SysLogFileInfo>> listLogFiles(SysLogQueryRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<SysLogQueryRequest, List<SysLogFileInfo>>() {

            @Override
            public void beforeService(SysLogQueryRequest param) {
                SysLogParamChecker.checkSysLogQueryRequest(param);
            }

            @Override
            public List<SysLogFileInfo> execute(SysLogQueryRequest param) {
                if (param == null){
                    param = new SysLogQueryRequest();
                }
                return sysLogManager.getLogFileList(param.getFileName(), param.getPageNum(), param.getPageSize());
            }

        });
    }

    /**
     * 查询日志详情（支持翻页和关键词搜索）
     */
    @GetMapping("/detail")
    @SaIgnore
    public ApiResult<SysLogFileDetail> getLogDetail(SysLogDetailRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<SysLogDetailRequest, SysLogFileDetail>() {

            @Override
            public void beforeService(SysLogDetailRequest param) {
                SysLogParamChecker.checkSysLogDetailRequest(request);
            }

            @Override
            public SysLogFileDetail execute(SysLogDetailRequest param) {
                String fileName = request.getFileName();
                int pageNum = request.getPageNum();
                int pageSize = request.getPageSize();
                String keyword = request.getKeyword();
                return sysLogManager.getLogDetail(fileName, pageNum, pageSize, keyword);
            }
        });
    }
}