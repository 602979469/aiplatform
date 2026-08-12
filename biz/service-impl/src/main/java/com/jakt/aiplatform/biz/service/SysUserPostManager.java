package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.core.model.domain.SysUserPost;
import com.jakt.aiplatform.core.model.param.SysUserPostQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 用户岗位关联管理类接口定义
 * 
 */
public interface SysUserPostManager {

    /**
     * 创建用户岗位关联
     *
     * @param sysUserPost 用户岗位关联
     * @return 创建成功后的用户岗位关联
     */
    SysUserPost createSysUserPost(SysUserPost sysUserPost);

    /**
     * 按 ID 查询用户岗位关联
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
    PageResult<SysUserPost> pageSysUserPosts(SysUserPostQueryParam query);

    /**
     * 列表查询用户岗位关联
     *
     * @param query 查询参数
     * @return 用户岗位关联列表
     */
    List<SysUserPost> listSysUserPosts(SysUserPostQueryParam query);

    /**
     * 更新用户岗位关联（全量）。
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
     * 删除用户岗位关联。
     *
     * @param id 用户岗位关联 ID
     */
    void deleteSysUserPost(Long id);
}
