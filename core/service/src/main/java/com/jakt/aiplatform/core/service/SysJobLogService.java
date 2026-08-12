package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.domain.SysJobLog;
import com.jakt.aiplatform.core.model.param.SysJobLogQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 定时任务日志领域服务
 *
 * 实现类为 SysJobLogServiceImpl（core.service.impl 包）。
 */
public interface SysJobLogService {

    /**
     * 创建定时任务日志
     *
     * @param sysJobLog 定时任务日志
     * @return 创建后的定时任务日志（主键已回填）
     */
    SysJobLog createSysJobLog(SysJobLog sysJobLog);

    /**
     * 更新定时任务日志（全量）
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
     * 删除定时任务日志
     *
     * @param id 定时任务日志 ID
     */
    void deleteSysJobLog(Long id);

    /**
     * 按 ID 获取定时任务日志
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
    PageResult<SysJobLog> findPage(SysJobLogQueryParam query);

    /**
     * 列表查询定时任务日志
     *
     * @param query 查询参数
     * @return 定时任务日志列表
     */
    List<SysJobLog> findList(SysJobLogQueryParam query);
}
