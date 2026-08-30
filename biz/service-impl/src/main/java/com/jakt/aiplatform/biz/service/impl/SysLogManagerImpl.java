package com.jakt.aiplatform.biz.service.impl;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.date.DateUtil;
import com.jakt.aiplatform.core.model.domain.SysLogFileDetail;
import com.jakt.aiplatform.core.model.domain.SysLogFileInfo;
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
import com.jakt.aiplatform.biz.service.SysLogManager;

@Service
public class SysLogManagerImpl implements SysLogManager {

    @Value("${log.file.path:/Users/jakt/IdeaProjects/aiplatform/logs/}")
    private String logFileDir;

    /**
     * 获取日志文件列表（支持文件名模糊搜索和分页）
     */
    @Override
    public List<SysLogFileInfo> getLogFileList(String fileName, Integer pageNum, Integer pageSize) {
        File logDir = FileUtil.file(logFileDir);
        if (!logDir.exists()) {
            return new ArrayList<>();
        }

        // 获取所有日志文件（支持.log和.log.gz）
        List<File> files = FileUtil.loopFiles(logDir, pathname ->
                pathname.getName().endsWith(".log") || pathname.getName().endsWith(".log.gz")
        );

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
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);

        if (start >= total) {
            return new ArrayList<>();
        }

        List<File> pageFiles = files.subList(start, end);

        // 转换为DTO
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

    /**
     * 获取日志详情（支持翻页和关键词搜索）
     * 使用RandomAccessFile流式读取，支持大文件
     */
    @Override
    public SysLogFileDetail getLogDetail(String fileName, Integer pageNum, Integer pageSize, String keyword) {
        if (StrUtil.isBlank(fileName)) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        String filePath = logFileDir + fileName;
        File logFile = FileUtil.file(filePath);

        if (!logFile.exists()) {
            throw new RuntimeException("日志文件不存在: " + fileName);
        }

        SysLogFileDetail detail = new SysLogFileDetail();
        detail.setFileName(fileName);
        detail.setFileSize(FileUtil.readableFileSize(logFile));
        detail.setLastModified(DateUtil.formatDateTime(FileUtil.lastModifiedTime(logFile)));
        detail.setPageNum(pageNum);
        detail.setPageSize(pageSize);

        List<String> content = new ArrayList<>();
        int totalLines = 0;
        int start = (pageNum - 1) * pageSize;
        int end = start + pageSize;

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
            throw new RuntimeException("读取日志文件失败: " + e.getMessage());
        }

        detail.setTotalLines(totalLines);
        detail.setContent(content);

        return detail;
    }
}
