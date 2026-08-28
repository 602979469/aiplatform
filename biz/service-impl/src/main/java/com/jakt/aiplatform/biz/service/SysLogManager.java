package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.core.model.domain.SysLogFileDetail;
import com.jakt.aiplatform.core.model.domain.SysLogFileInfo;

import java.util.List;

public interface SysLogManager {


    /**
     * 获取日志文件列表
     * @param fileName 日志文件名
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 日志文件列表
     */
    List<SysLogFileInfo> getLogFileList(String fileName, Integer pageNum, Integer pageSize);

    /**
     * 获取日志文件详情
     * @param fileName 日志文件名
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param keyword 关键字
     * @return 日志文件详情
     */
    SysLogFileDetail getLogDetail(String fileName, Integer pageNum, Integer pageSize, String keyword);
}
