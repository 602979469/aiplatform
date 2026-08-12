package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysPost;
import com.jakt.aiplatform.core.model.param.SysPostQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 岗位仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
public interface SysPostRepository {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 岗位领域模型
     */
    SysPost findById(Long id);

    /**
     * 分页查询。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysPost> findPage(SysPostQueryParam query);

    /**
     * 列表查询。
     *
     * @param query 查询参数
     * @return 岗位列表
     */
    List<SysPost> findList(SysPostQueryParam query);

    /**
     * 新增。
     *
     * @param sysPost 岗位
     * @return 新增后的岗位（主键已回填）
     */
    SysPost insert(SysPost sysPost);

    /**
     * 更新。
     *
     * @param sysPost 岗位
     */
    void update(SysPost sysPost);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新）。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；create_time/update_time 由数据库自动维护。
     *
     * @param sysPost 岗位（至少含主键）
     */
    void updateByCondition(SysPost sysPost);

    /**
     * 按主键删除。
     *
     * @param id 主键
     */
    void deleteById(Long id);
}
