package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysLogininfor;
import com.jakt.aiplatform.core.model.param.SysLogininforQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 登录日志仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
public interface SysLogininforRepository {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 登录日志领域模型
     */
    SysLogininfor findById(Long id);

    /**
     * 分页查询。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysLogininfor> findPage(SysLogininforQueryParam query);

    /**
     * 列表查询。
     *
     * @param query 查询参数
     * @return 登录日志列表
     */
    List<SysLogininfor> findList(SysLogininforQueryParam query);

    /**
     * 新增。
     *
     * @param sysLogininfor 登录日志
     * @return 新增后的登录日志（主键已回填）
     */
    SysLogininfor insert(SysLogininfor sysLogininfor);

    /**
     * 更新。
     *
     * @param sysLogininfor 登录日志
     */
    void update(SysLogininfor sysLogininfor);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新）。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；create_time/update_time 由数据库自动维护。
     *
     * @param sysLogininfor 登录日志（至少含主键）
     */
    void updateByCondition(SysLogininfor sysLogininfor);

    /**
     * 按主键删除。
     *
     * @param id 主键
     */
    void deleteById(Long id);
}
