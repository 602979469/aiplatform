package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.FileInfo;
import com.jakt.aiplatform.core.model.param.FileInfoQueryParam;

import java.io.File;
import java.util.List;

/**
 * 文件管理业务编排：上传 / 列表 / 下载 / 更新 / 删除 / 替换，一一对应 Controller 用例。
 */
public interface FileInfoManager {

    /**
     * 查询可用业务命名空间列表（下拉框数据源）。
     *
     * <p>优先读环境变量 AIPLATFORM_FILE_NAMESPACES（逗号分隔），未配置时取 FileNamespaceEnum 默认值。
     *
     * @return 命名空间列表
     */
    List<String> listNamespaces();

    /**
     * 上传文件。
     *
     * @param namespace    业务命名空间
     * @param content      文件字节
     * @param originalName 原始文件名（含扩展名）
     * @param remark       备注
     * @return 上传后的文件信息
     */
    FileInfo upload(String namespace, byte[] content, String originalName, String remark);

    /**
     * 按 namespace 分页查询文件列表。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<FileInfo> page(FileInfoQueryParam query);

    /**
     * 获取文件元信息。
     *
     * @param id        文件主键
     * @param namespace 业务命名空间
     * @return 文件信息
     */
    FileInfo getFile(Long id, String namespace);

    /**
     * 获取文件下载用磁盘文件。
     *
     * @param id        文件主键
     * @param namespace 业务命名空间
     * @return 磁盘文件
     */
    File downloadFile(Long id, String namespace);

    /**
     * 更新文件元信息（改名/备注）。
     *
     * @param id           文件主键
     * @param namespace    业务命名空间
     * @param originalName 新的原始文件名；为空表示不修改
     * @param remark       新的备注；为空表示不修改
     */
    void update(Long id, String namespace, String originalName, String remark);

    /**
     * 删除文件。
     *
     * @param id        文件主键
     * @param namespace 业务命名空间
     */
    void delete(Long id, String namespace);

    /**
     * 替换文件内容。
     *
     * @param id           文件主键
     * @param namespace    业务命名空间
     * @param content      新文件字节
     * @param originalName 新原始文件名（含扩展名）
     * @return 替换后的文件信息
     */
    FileInfo replace(Long id, String namespace, byte[] content, String originalName);
}
