package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.core.model.domain.SysJob;
import com.jakt.aiplatform.core.model.param.SysJobQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 定时任务管理类接口定义
 * 
 */
public interface SysJobManager {

    /**
     * 创建定时任务
     *
     * @param sysJob 定时任务
     * @return 创建成功后的定时任务
     */
    SysJob createSysJob(SysJob sysJob);

    /**
     * 按 ID 查询定时任务
     *
     * @param id 定时任务 ID
     * @return 定时任务
     */
    SysJob getSysJob(Long id);

    /**
     * 分页查询定时任务
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysJob> pageSysJobs(SysJobQueryParam query);

    /**
     * 列表查询定时任务
     *
     * @param query 查询参数
     * @return 定时任务列表
     */
    List<SysJob> listSysJobs(SysJobQueryParam query);

    /**
     * 更新定时任务（全量）。
     *
     * @param sysJob 定时任务（含主键）
     */
    void updateSysJob(SysJob sysJob);

    /**
     * 按条件更新定时任务（只更新传入的非空字段）。
     *
     * @param sysJob 定时任务（至少含主键）
     */
    void updateByCondition(SysJob sysJob);

    /**
     * 删除定时任务。
     *
     * @param id 定时任务 ID
     */
    void deleteSysJob(Long id);
}
