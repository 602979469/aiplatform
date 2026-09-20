package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysLogManager;
import com.jakt.aiplatform.core.model.domain.SysLogFileDetail;
import com.jakt.aiplatform.core.model.domain.SysLogFileInfo;
import com.jakt.aiplatform.core.service.SysLogService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统日志用例编排：文件读取规则下沉 core-service。
 */
@Service
public class SysLogManagerImpl implements SysLogManager {

    /** 系统日志领域服务。 */
    private final SysLogService sysLogService;

    public SysLogManagerImpl(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }

    @Override
    public List<SysLogFileInfo> getLogFileList(String fileName, Integer pageNum, Integer pageSize) {
        return sysLogService.list(fileName, pageNum, pageSize);
    }

    @Override
    public SysLogFileDetail getLogDetail(String fileName, Integer pageNum, Integer pageSize, String keyword) {
        return sysLogService.detail(fileName, pageNum, pageSize, keyword);
    }
}
