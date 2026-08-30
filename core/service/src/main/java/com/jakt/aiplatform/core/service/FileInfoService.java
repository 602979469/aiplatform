package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.FileInfo;
import com.jakt.aiplatform.core.model.param.FileInfoQueryParam;

import java.io.InputStream;

/**
 * 文件信息表领域服务：承载文件存储与元数据的业务规则。
 */
public interface FileInfoService {

    /**
     * 上传文件（内容写入 MinIO，元数据落库）。
     *
     * @param namespace    业务命名空间
     * @param content      文件字节
     * @param originalName 原始文件名（含扩展名）
     * @param remark       备注
     * @return 上传后的文件信息（主键已回填）
     */
    FileInfo upload(String namespace, byte[] content, String originalName, String remark);

    /**
     * 流式上传文件（大文件走 MinIO 流式写入，不占内存）。
     *
     * @param namespace    业务命名空间
     * @param content      内容流（由调用方关闭）
     * @param size         内容字节数
     * @param originalName 原始文件名（含扩展名）
     * @param remark       备注
     * @return 上传后的文件信息（主键已回填）
     */
    FileInfo uploadStream(String namespace, InputStream content, long size, String originalName, String remark);

    /**
     * 按 namespace 分页查询文件列表。
     *
     * @param query 查询参数（namespace 必填）
     * @return 分页结果
     */
    PageResult<FileInfo> findPage(FileInfoQueryParam query);

    /**
     * 获取文件元信息（校验 namespace 归属）。
     *
     * @param id        文件主键
     * @param namespace 业务命名空间
     * @return 文件信息
     */
    FileInfo getFile(Long id, String namespace);

    /**
     * 打开文件内容流（校验 namespace 归属，从 MinIO 流式读取；调用方负责关闭）。
     *
     * @param id        文件主键
     * @param namespace 业务命名空间
     * @return 内容流
     */
    InputStream openContentStream(Long id, String namespace);

    /**
     * 获取文件内容大小（下载 Content-Length 用）。
     *
     * @param id        文件主键
     * @param namespace 业务命名空间
     * @return 内容字节数
     */
    long getContentSize(Long id, String namespace);

    /**
     * 更新文件元信息（改名/备注，不迁移 namespace）。
     *
     * @param id           文件主键
     * @param namespace    业务命名空间
     * @param originalName 新的原始文件名；为空表示不修改
     * @param remark       新的备注；为空表示不修改
     */
    void updateMeta(Long id, String namespace, String originalName, String remark);

    /**
     * 删除文件：先删磁盘文件，再删元数据。
     *
     * @param id        文件主键
     * @param namespace 业务命名空间
     */
    void delete(Long id, String namespace);

    /**
     * 替换文件内容：新内容落盘并更新元数据后删除旧文件。
     *
     * @param id           文件主键
     * @param namespace    业务命名空间
     * @param content      新文件字节
     * @param originalName 新原始文件名（含扩展名）
     * @return 替换后的文件信息
     */
    FileInfo replace(Long id, String namespace, byte[] content, String originalName);
}
