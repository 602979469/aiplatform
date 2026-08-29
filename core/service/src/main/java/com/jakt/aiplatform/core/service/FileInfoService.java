package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.FileInfo;
import com.jakt.aiplatform.core.model.param.FileInfoQueryParam;

import java.io.File;

/**
 * 文件信息表领域服务：承载文件存储与元数据的业务规则。
 */
public interface FileInfoService {

    /**
     * 上传文件：内容落盘后写入元数据。
     *
     * @param namespace    业务命名空间
     * @param content      文件字节
     * @param originalName 原始文件名（含扩展名）
     * @param remark       备注
     * @return 上传后的文件信息（主键已回填）
     */
    FileInfo upload(String namespace, byte[] content, String originalName, String remark);

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
     * 解析文件下载用磁盘文件（校验 namespace 归属）。
     *
     * @param id        文件主键
     * @param namespace 业务命名空间
     * @return 磁盘文件
     */
    File resolveFile(Long id, String namespace);

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
