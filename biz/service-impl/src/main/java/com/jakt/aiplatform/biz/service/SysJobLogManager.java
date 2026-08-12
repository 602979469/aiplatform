package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.core.model.domain.SysJobLog;
import com.jakt.aiplatform.core.model.param.SysJobLogQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 定时任务日志管理类接口定义
 * 
 */
public interface SysJobLogManager {

    /**
     * 创建定时任务日志
     *
     * @param sysJobLog 定时任务日志
     * @return 创建成功后的定时任务日志
     */
    SysJobLog createSysJobLog(SysJobLog sysJobLog);

    /**
     * 按 ID 查询定时任务日志
     *
     * @param id 定时任务日志 ID
     * @return 定时任务日志
     */
    SysJobLog getSysJobLog(Long id);

    /**
     * 分页查询定时任务日志
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysJobLog> pageSysJobLogs(SysJobLogQueryParam query);

    /**
     * 列表查询定时任务日志
     *
     * @param query 查询参数
     * @return 定时任务日志列表
     */
    List<SysJobLog> listSysJobLogs(SysJobLogQueryParam query);

    /**
     * 更新定时任务日志（全量）。
     *
     * @param sysJobLog 定时任务日志（含主键）
     */
    void updateSysJobLog(SysJobLog sysJobLog);

    /**
     * 按条件更新定时任务日志（只更新传入的非空字段）。
     *
     * @param sysJobLog 定时任务日志（至少含主键）
     */
    void updateByCondition(SysJobLog sysJobLog);

    /**
     * 删除定时任务日志。
     *
     * @param id 定时任务日志 ID
     */
    void deleteSysJobLog(Long id);
}
