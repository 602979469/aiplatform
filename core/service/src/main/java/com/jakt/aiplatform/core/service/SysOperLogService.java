package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.domain.SysOperLog;
import com.jakt.aiplatform.core.model.param.SysOperLogQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 操作日志领域服务
 *
 * 实现类为 SysOperLogServiceImpl（core.service.impl 包）。
 */
public interface SysOperLogService {

    /**
     * 创建操作日志
     *
     * @param sysOperLog 操作日志
     * @return 创建后的操作日志（主键已回填）
     */
    SysOperLog createSysOperLog(SysOperLog sysOperLog);

    /**
     * 更新操作日志（全量）
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
     * 删除操作日志
     *
     * @param id 操作日志 ID
     */
    void deleteSysOperLog(Long id);

    /**
     * 按 ID 获取操作日志
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
    PageResult<SysOperLog> findPage(SysOperLogQueryParam query);

    /**
     * 列表查询操作日志
     *
     * @param query 查询参数
     * @return 操作日志列表
     */
    List<SysOperLog> findList(SysOperLogQueryParam query);
}
