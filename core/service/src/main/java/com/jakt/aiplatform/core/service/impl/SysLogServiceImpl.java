package com.jakt.aiplatform.core.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.framework.exception.AiPlatformException;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.core.model.domain.SysLogFileDetail;
import com.jakt.aiplatform.core.model.domain.SysLogFileInfo;
import com.jakt.aiplatform.core.model.enums.BizErrorCodeEnum;
import com.jakt.aiplatform.core.service.SysLogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统日志文件领域服务实现：直接读取日志目录，不做持久化。
 */
@Service
public class SysLogServiceImpl implements SysLogService {

    /** 默认页码。 */
    private static final int DEFAULT_PAGE_NUM = 1;

    /** 默认每页条数。 */
    private static final int DEFAULT_PAGE_SIZE = 10;

    /** 日志文件目录。 */
    private final String logFileDir;

    public SysLogServiceImpl(@Value("${log.file.path}") String logFileDir) {
        this.logFileDir = logFileDir;
    }

    @Override
    public List<SysLogFileInfo> list(String fileName, Integer pageNum, Integer pageSize) {
        File logDir = FileUtil.file(logFileDir);
        if (!logDir.exists()) {
            return new ArrayList<>();
        }

        // 获取所有日志文件（支持 .log 和 .log.gz）
        List<File> files = FileUtil.loopFiles(logDir, pathname ->
                pathname.getName().endsWith(".log") || pathname.getName().endsWith(".log.gz"));

        // 文件名过滤
        if (StrUtil.isNotBlank(fileName)) {
            files = files.stream()
                    .filter(file -> file.getName().contains(fileName))
                    .collect(Collectors.toList());
        }

        // 按文件名倒序排列（最新的在前面）
        files.sort((a, b) -> b.getName().compareTo(a.getName()));

        // 分页处理
        int total = files.size();
        int safePageNum = ObjectUtil.defaultIfNull(pageNum, DEFAULT_PAGE_NUM);
        int safePageSize = ObjectUtil.defaultIfNull(pageSize, DEFAULT_PAGE_SIZE);
        int start = (safePageNum - 1) * safePageSize;
        int end = Math.min(start + safePageSize, total);
        if (start < 0 || start >= total) {
            return new ArrayList<>();
        }

        List<File> pageFiles = files.subList(start, end);
        return pageFiles.stream()
                .map(file -> {
                    SysLogFileInfo info = new SysLogFileInfo();
                    info.setFileName(file.getName());
                    info.setFilePath(file.getAbsolutePath());
                    info.setFileSize(FileUtil.readableFileSize(file));
                    info.setLastModified(DateUtil.formatDateTime(FileUtil.lastModifiedTime(file)));
                    return info;
                })
                .collect(Collectors.toList());
    }

    @Override
    public SysLogFileDetail detail(String fileName, Integer pageNum, Integer pageSize, String keyword) {
        AssertUtil.throwErrWhenBlank(fileName, ErrorCodeEnum.PARAM_INVALID, "文件名不能为空");

        File logFile = FileUtil.file(logFileDir + fileName);
        AssertUtil.throwErrWhenFalse(logFile.exists(), BizErrorCodeEnum.RESOURCE_NOT_FOUND,
                "日志文件不存在: " + fileName);

        int safePageNum = ObjectUtil.defaultIfNull(pageNum, DEFAULT_PAGE_NUM);
        int safePageSize = ObjectUtil.defaultIfNull(pageSize, DEFAULT_PAGE_SIZE);

        SysLogFileDetail detail = new SysLogFileDetail();
        detail.setFileName(fileName);
        detail.setFileSize(FileUtil.readableFileSize(logFile));
        detail.setLastModified(DateUtil.formatDateTime(FileUtil.lastModifiedTime(logFile)));
        detail.setPageNum(safePageNum);
        detail.setPageSize(safePageSize);

        List<String> content = new ArrayList<>();
        int totalLines = 0;
        int start = (safePageNum - 1) * safePageSize;
        int end = start + safePageSize;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(logFile), StandardCharsets.UTF_8))) {
            String line;
            int lineCount = 0;
            while ((line = reader.readLine()) != null) {
                if (StrUtil.isNotBlank(keyword) && !line.contains(keyword)) {
                    continue;
                }
                totalLines++;
                if (lineCount >= start && lineCount < end) {
                    content.add(line);
                }
                lineCount++;
            }
        } catch (Exception e) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.SYSTEM_ERROR, "读取日志文件失败", e);
        }

        detail.setTotalLines(totalLines);
        detail.setContent(content);
        return detail;
    }
}
