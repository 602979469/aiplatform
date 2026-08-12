package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysUserOnline;
import com.jakt.aiplatform.core.model.param.SysUserOnlineQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 在线用户仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
public interface SysUserOnlineRepository {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 在线用户领域模型
     */
    SysUserOnline findById(String id);

    /**
     * 分页查询。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysUserOnline> findPage(SysUserOnlineQueryParam query);

    /**
     * 列表查询。
     *
     * @param query 查询参数
     * @return 在线用户列表
     */
    List<SysUserOnline> findList(SysUserOnlineQueryParam query);

    /**
     * 新增。
     *
     * @param sysUserOnline 在线用户
     * @return 新增后的在线用户（主键已回填）
     */
    SysUserOnline insert(SysUserOnline sysUserOnline);

    /**
     * 更新。
     *
     * @param sysUserOnline 在线用户
     */
    void update(SysUserOnline sysUserOnline);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新）。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；create_time/update_time 由数据库自动维护。
     *
     * @param sysUserOnline 在线用户（至少含主键）
     */
    void updateByCondition(SysUserOnline sysUserOnline);

    /**
     * 按主键删除。
     *
     * @param id 主键
     */
    void deleteById(String id);
}
