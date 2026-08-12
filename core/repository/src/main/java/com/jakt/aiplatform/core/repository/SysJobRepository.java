package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysJob;
import com.jakt.aiplatform.core.model.param.SysJobQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 定时任务仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
public interface SysJobRepository {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 定时任务领域模型
     */
    SysJob findById(Long id);

    /**
     * 分页查询。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysJob> findPage(SysJobQueryParam query);

    /**
     * 列表查询。
     *
     * @param query 查询参数
     * @return 定时任务列表
     */
    List<SysJob> findList(SysJobQueryParam query);

    /**
     * 新增。
     *
     * @param sysJob 定时任务
     * @return 新增后的定时任务（主键已回填）
     */
    SysJob insert(SysJob sysJob);

    /**
     * 更新。
     *
     * @param sysJob 定时任务
     */
    void update(SysJob sysJob);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新）。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；create_time/update_time 由数据库自动维护。
     *
     * @param sysJob 定时任务（至少含主键）
     */
    void updateByCondition(SysJob sysJob);

    /**
     * 按主键删除。
     *
     * @param id 主键
     */
    void deleteById(Long id);
}
