package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.core.model.domain.SysUser;
import com.jakt.aiplatform.core.model.param.SysUserQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 用户管理类接口定义
 * 
 */
public interface SysUserManager {

    /**
     * 创建用户
     *
     * @param sysUser 用户
     * @return 创建成功后的用户
     */
    SysUser createSysUser(SysUser sysUser);

    /**
     * 按 ID 查询用户
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
    PageResult<SysUser> pageSysUsers(SysUserQueryParam query);

    /**
     * 列表查询用户
     *
     * @param query 查询参数
     * @return 用户列表
     */
    List<SysUser> listSysUsers(SysUserQueryParam query);

    /**
     * 更新用户（全量）。
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
     * 删除用户。
     *
     * @param id 用户 ID
     */
    void deleteSysUser(Long id);
}
