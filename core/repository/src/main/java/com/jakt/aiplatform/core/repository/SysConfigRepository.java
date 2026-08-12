package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysConfig;
import com.jakt.aiplatform.core.model.param.SysConfigQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 参数配置仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
public interface SysConfigRepository {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 参数配置领域模型
     */
    SysConfig findById(Long id);

    /**
     * 分页查询。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysConfig> findPage(SysConfigQueryParam query);

    /**
     * 列表查询。
     *
     * @param query 查询参数
     * @return 参数配置列表
     */
    List<SysConfig> findList(SysConfigQueryParam query);

    /**
     * 新增。
     *
     * @param sysConfig 参数配置
     * @return 新增后的参数配置（主键已回填）
     */
    SysConfig insert(SysConfig sysConfig);

    /**
     * 更新。
     *
     * @param sysConfig 参数配置
     */
    void update(SysConfig sysConfig);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新）。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；create_time/update_time 由数据库自动维护。
     *
     * @param sysConfig 参数配置（至少含主键）
     */
    void updateByCondition(SysConfig sysConfig);

    /**
     * 按主键删除。
     *
     * @param id 主键
     */
    void deleteById(Long id);
}
