package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysOperLog;
import com.jakt.aiplatform.core.model.param.SysOperLogQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 操作日志仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
public interface SysOperLogRepository {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 操作日志领域模型
     */
    SysOperLog findById(Long id);

    /**
     * 分页查询。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysOperLog> findPage(SysOperLogQueryParam query);

    /**
     * 列表查询。
     *
     * @param query 查询参数
     * @return 操作日志列表
     */
    List<SysOperLog> findList(SysOperLogQueryParam query);

    /**
     * 新增。
     *
     * @param sysOperLog 操作日志
     * @return 新增后的操作日志（主键已回填）
     */
    SysOperLog insert(SysOperLog sysOperLog);

    /**
     * 更新。
     *
     * @param sysOperLog 操作日志
     */
    void update(SysOperLog sysOperLog);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新）。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；create_time/update_time 由数据库自动维护。
     *
     * @param sysOperLog 操作日志（至少含主键）
     */
    void updateByCondition(SysOperLog sysOperLog);

    /**
     * 按主键删除。
     *
     * @param id 主键
     */
    void deleteById(Long id);
}
