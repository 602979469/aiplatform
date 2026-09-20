package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.domain.SysLogFileDetail;
import com.jakt.aiplatform.core.model.domain.SysLogFileInfo;

import java.util.List;

/**
 * 系统日志文件领域服务：日志文件列表与详情（支持翻页与关键词搜索）。
 */
public interface SysLogService {

    /**
     * 获取日志文件列表（支持文件名模糊搜索和分页）。
     *
     * @param fileName 日志文件名（可空）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 日志文件列表
     */
    List<SysLogFileInfo> list(String fileName, Integer pageNum, Integer pageSize);

    /**
     * 获取日志文件详情（支持翻页和关键词搜索）。
     *
     * @param fileName 日志文件名
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param keyword 关键字（可空）
     * @return 日志文件详情
     */
    SysLogFileDetail detail(String fileName, Integer pageNum, Integer pageSize, String keyword);
}
