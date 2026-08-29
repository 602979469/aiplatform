package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.FileInfo;
import com.jakt.aiplatform.core.model.param.FileInfoQueryParam;

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
     * 按主键删除。
     *
     * @param id 主键
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int deleteById(Long id);
}
