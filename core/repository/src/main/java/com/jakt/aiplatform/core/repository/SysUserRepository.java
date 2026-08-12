package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysUser;
import com.jakt.aiplatform.core.model.param.SysUserQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 用户仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
public interface SysUserRepository {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 用户领域模型
     */
    SysUser findById(Long id);

    /**
     * 分页查询。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysUser> findPage(SysUserQueryParam query);

    /**
     * 列表查询。
     *
     * @param query 查询参数
     * @return 用户列表
     */
    List<SysUser> findList(SysUserQueryParam query);

    /**
     * 新增。
     *
     * @param sysUser 用户
     * @return 新增后的用户（主键已回填）
     */
    SysUser insert(SysUser sysUser);

    /**
     * 更新。
     *
     * @param sysUser 用户
     */
    void update(SysUser sysUser);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新）。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；create_time/update_time 由数据库自动维护。
     *
     * @param sysUser 用户（至少含主键）
     */
    void updateByCondition(SysUser sysUser);

    /**
     * 按主键删除。
     *
     * @param id 主键
     */
    void deleteById(Long id);
}
