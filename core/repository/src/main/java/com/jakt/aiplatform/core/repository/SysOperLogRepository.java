package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysOperLog;

import java.util.List;

/**
 * 操作日志仓储（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
public interface SysOperLogRepository {

    /**
     * 新增操作日志。
     *
     * @param operLog 操作日志
     * @return 影响行数
     */
    int insertOperlog(SysOperLog operLog);

    /**
     * 按条件查询操作日志列表。
     *
     * @param operLog 查询条件（实体即条件）
     * @return 操作日志列表
     */
    List<SysOperLog> selectOperLogList(SysOperLog operLog);

    /**
     * 按 ID 集合批量删除操作日志。
     *
     * @param ids 操作日志ID集合（逗号分隔）
     * @return 影响行数
     */
    int deleteOperLogByIds(String ids);

    /**
     * 按主键查询操作日志。
     *
     * @param operId 操作日志ID
     * @return 操作日志领域模型
     */
    SysOperLog selectOperLogById(Long operId);

    /**
     * 清空操作日志。
     *
     * @return 影响行数
     */
    int cleanOperLog();
}
