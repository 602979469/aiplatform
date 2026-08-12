package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.core.model.domain.SysOperLog;
import com.jakt.aiplatform.core.model.param.SysOperLogQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 操作日志管理类接口定义
 * 
 */
public interface SysOperLogManager {

    /**
     * 创建操作日志
     *
     * @param sysOperLog 操作日志
     * @return 创建成功后的操作日志
     */
    SysOperLog createSysOperLog(SysOperLog sysOperLog);

    /**
     * 按 ID 查询操作日志
     *
     * @param id 操作日志 ID
     * @return 操作日志
     */
    SysOperLog getSysOperLog(Long id);

    /**
     * 分页查询操作日志
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysOperLog> pageSysOperLogs(SysOperLogQueryParam query);

    /**
     * 列表查询操作日志
     *
     * @param query 查询参数
     * @return 操作日志列表
     */
    List<SysOperLog> listSysOperLogs(SysOperLogQueryParam query);

    /**
     * 更新操作日志（全量）。
     *
     * @param sysOperLog 操作日志（含主键）
     */
    void updateSysOperLog(SysOperLog sysOperLog);

    /**
     * 按条件更新操作日志（只更新传入的非空字段）。
     *
     * @param sysOperLog 操作日志（至少含主键）
     */
    void updateByCondition(SysOperLog sysOperLog);

    /**
     * 删除操作日志。
     *
     * @param id 操作日志 ID
     */
    void deleteSysOperLog(Long id);
}
