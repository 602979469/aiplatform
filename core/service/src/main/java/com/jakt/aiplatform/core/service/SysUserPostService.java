package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.domain.SysUserPost;
import com.jakt.aiplatform.core.model.param.SysUserPostQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 用户岗位关联领域服务
 *
 * 实现类为 SysUserPostServiceImpl（core.service.impl 包）。
 */
public interface SysUserPostService {

    /**
     * 创建用户岗位关联
     *
     * @param sysUserPost 用户岗位关联
     * @return 创建后的用户岗位关联（主键已回填）
     */
    SysUserPost createSysUserPost(SysUserPost sysUserPost);

    /**
     * 更新用户岗位关联（全量）
     *
     * @param sysUserPost 用户岗位关联（含主键）
     */
    void updateSysUserPost(SysUserPost sysUserPost);

    /**
     * 按条件更新用户岗位关联（只更新传入的非空字段）。
     *
     * @param sysUserPost 用户岗位关联（至少含主键）
     */
    void updateByCondition(SysUserPost sysUserPost);

    /**
     * 删除用户岗位关联
     *
     * @param id 用户岗位关联 ID
     */
    void deleteSysUserPost(Long id);

    /**
     * 按 ID 获取用户岗位关联
     *
     * @param id 用户岗位关联 ID
     * @return 用户岗位关联
     */
    SysUserPost getSysUserPost(Long id);

    /**
     * 分页查询用户岗位关联
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysUserPost> findPage(SysUserPostQueryParam query);

    /**
     * 列表查询用户岗位关联
     *
     * @param query 查询参数
     * @return 用户岗位关联列表
     */
    List<SysUserPost> findList(SysUserPostQueryParam query);
}
