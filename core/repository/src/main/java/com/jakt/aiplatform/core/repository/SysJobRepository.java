package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysJob;

import java.util.List;

/**
 * 定时任务仓储（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
public interface SysJobRepository {

    /**
     * 按条件查询定时任务列表。
     *
     * @param job 查询条件（实体即条件）
     * @return 定时任务列表
     */
    List<SysJob> selectJobList(SysJob job);

    /**
     * 查询全部定时任务。
     *
     * @return 定时任务列表
     */
    List<SysJob> selectJobAll();

    /**
     * 按主键查询定时任务。
     *
     * @param jobId 任务ID
     * @return 定时任务领域模型
     */
    SysJob selectJobById(Long jobId);

    /**
     * 按主键删除定时任务。
     *
     * @param jobId 任务ID
     * @return 影响行数
     */
    int deleteJobById(Long jobId);

    /**
     * 按 ID 集合批量删除定时任务。
     *
     * @param ids 任务ID集合（逗号分隔）
     * @return 影响行数
     */
    int deleteJobByIds(String ids);

    /**
     * 全量更新定时任务。
     *
     * @param job 定时任务
     * @return 影响行数
     */
    int updateJob(SysJob job);

    /**
     * 新增定时任务。
     *
     * @param job 定时任务
     * @return 影响行数
     */
    int insertJob(SysJob job);
}
