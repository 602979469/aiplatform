package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysJobLog;
import com.jakt.aiplatform.core.model.param.SysJobLogQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 定时任务日志仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
public interface SysJobLogRepository {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 定时任务日志领域模型
     */
    SysJobLog findById(Long id);

    /**
     * 分页查询。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysJobLog> findPage(SysJobLogQueryParam query);

    /**
     * 列表查询。
     *
     * @param query 查询参数
     * @return 定时任务日志列表
     */
    List<SysJobLog> findList(SysJobLogQueryParam query);

    /**
     * 新增。
     *
     * @param sysJobLog 定时任务日志
     * @return 新增后的定时任务日志（主键已回填）
     */
    SysJobLog insert(SysJobLog sysJobLog);

    /**
     * 更新。
     *
     * @param sysJobLog 定时任务日志
     */
    void update(SysJobLog sysJobLog);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新）。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；create_time/update_time 由数据库自动维护。
     *
     * @param sysJobLog 定时任务日志（至少含主键）
     */
    void updateByCondition(SysJobLog sysJobLog);

    /**
     * 按主键删除。
     *
     * @param id 主键
     */
    void deleteById(Long id);
}
