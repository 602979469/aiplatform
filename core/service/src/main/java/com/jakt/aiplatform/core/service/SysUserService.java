package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.domain.SysUser;
import com.jakt.aiplatform.core.model.param.SysUserQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 用户领域服务
 *
 * 实现类为 SysUserServiceImpl（core.service.impl 包）。
 */
public interface SysUserService {

    /**
     * 创建用户
     *
     * @param sysUser 用户
     * @return 创建后的用户（主键已回填）
     */
    SysUser createSysUser(SysUser sysUser);

    /**
     * 更新用户（全量）
     *
     * @param sysUser 用户（含主键）
     */
    void updateSysUser(SysUser sysUser);

    /**
     * 按条件更新用户（只更新传入的非空字段）。
     *
     * @param sysUser 用户（至少含主键）
     */
    void updateByCondition(SysUser sysUser);

    /**
     * 删除用户
     *
     * @param id 用户 ID
     */
    void deleteSysUser(Long id);

    /**
     * 按 ID 获取用户
     *
     * @param id 用户 ID
     * @return 用户
     */
    SysUser getSysUser(Long id);

    /**
     * 分页查询用户
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysUser> findPage(SysUserQueryParam query);

    /**
     * 列表查询用户
     *
     * @param query 查询参数
     * @return 用户列表
     */
    List<SysUser> findList(SysUserQueryParam query);
}
