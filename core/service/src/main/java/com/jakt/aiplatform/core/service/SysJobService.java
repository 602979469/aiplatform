package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.domain.SysJob;
import com.jakt.aiplatform.core.model.param.SysJobQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 定时任务领域服务
 *
 * 实现类为 SysJobServiceImpl（core.service.impl 包）。
 */
public interface SysJobService {

    /**
     * 创建定时任务
     *
     * @param sysJob 定时任务
     * @return 创建后的定时任务（主键已回填）
     */
    SysJob createSysJob(SysJob sysJob);

    /**
     * 更新定时任务（全量）
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
     * 删除定时任务
     *
     * @param id 定时任务 ID
     */
    void deleteSysJob(Long id);

    /**
     * 按 ID 获取定时任务
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
    PageResult<SysJob> findPage(SysJobQueryParam query);

    /**
     * 列表查询定时任务
     *
     * @param query 查询参数
     * @return 定时任务列表
     */
    List<SysJob> findList(SysJobQueryParam query);
}
