package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.FileInfo;
import com.jakt.aiplatform.core.model.param.FileInfoQueryParam;

import java.util.List;

/**
 * 文件信息表仓储：封装 Mapper，对外只暴露领域模型，不暴露 DO/DalQuery/DalResult。
 */
public interface FileInfoRepository {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 文件信息表领域模型；不存在返回 null
     */
    FileInfo findById(Long id);

    /**
     * 按主键查询文件内容（列表查询不加载大字段）。
     *
     * @param id 主键
     * @return 文件内容字节；不存在或内容为空返回 null
     */
    byte[] findContent(Long id);

    /**
     * 按命名空间 + 原始文件名精确查询（存量判断）。
     *
     * @param namespace    业务命名空间
     * @param originalName 原始文件名
     * @return 文件信息；不存在返回 null
     */
    FileInfo findOne(String namespace, String originalName);

    /**
     * 查询命名空间下全部原始文件名（不加载内容）。
     *
     * @param namespace 业务命名空间
     * @return 原始文件名列表
     */
    List<String> findOriginalNames(String namespace);

    /**
     * 分页查询。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<FileInfo> findPage(FileInfoQueryParam query);

    /**
     * 新增。
     *
     * @param fileInfo 文件信息表
     * @return 新增后的文件信息表；主键已回填到入参，返回同一对象
     */
    FileInfo insert(FileInfo fileInfo);

    /**
     * 更新（全量）。
     *
     * @param fileInfo 文件信息表（含主键）
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int update(FileInfo fileInfo);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新），用于元信息修改，不更新文件内容。
     *
     * @param fileInfo 文件信息表（含主键）
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int updateByCondition(FileInfo fileInfo);

    /**
     * 按主键删除。
     *
     * @param id 主键
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int deleteById(Long id);
}
