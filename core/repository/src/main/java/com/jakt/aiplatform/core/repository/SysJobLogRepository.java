package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysJobLog;

import java.util.List;

/**
 * 定时任务日志仓储（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
public interface SysJobLogRepository {

    /**
     * 按条件查询定时任务日志列表。
     *
     * @param jobLog 查询条件（实体即条件）
     * @return 定时任务日志列表
     */
    List<SysJobLog> selectJobLogList(SysJobLog jobLog);

    /**
     * 查询全部定时任务日志。
     *
     * @return 定时任务日志列表
     */
    List<SysJobLog> selectJobLogAll();

    /**
     * 按主键查询定时任务日志。
     *
     * @param jobLogId 任务日志ID
     * @return 定时任务日志领域模型
     */
    SysJobLog selectJobLogById(Long jobLogId);

    /**
     * 新增定时任务日志。
     *
     * @param jobLog 定时任务日志
     * @return 影响行数
     */
    int insertJobLog(SysJobLog jobLog);

    /**
     * 按 ID 集合批量删除定时任务日志。
     *
     * @param ids 任务日志ID集合（逗号分隔）
     * @return 影响行数
     */
    int deleteJobLogByIds(String ids);

    /**
     * 按主键删除定时任务日志。
     *
     * @param jobLogId 任务日志ID
     * @return 影响行数
     */
    int deleteJobLogById(Long jobLogId);

    /**
     * 清空定时任务日志。
     *
     * @return 影响行数
     */
    int cleanJobLog();
}
