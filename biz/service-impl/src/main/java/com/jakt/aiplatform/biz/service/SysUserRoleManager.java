package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.core.model.domain.SysUserRole;
import com.jakt.aiplatform.core.model.param.SysUserRoleQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 用户角色关联管理类接口定义
 * 
 */
public interface SysUserRoleManager {

    /**
     * 创建用户角色关联
     *
     * @param sysUserRole 用户角色关联
     * @return 创建成功后的用户角色关联
     */
    SysUserRole createSysUserRole(SysUserRole sysUserRole);

    /**
     * 按 ID 查询用户角色关联
     *
     * @param id 用户角色关联 ID
     * @return 用户角色关联
     */
    SysUserRole getSysUserRole(Long id);

    /**
     * 分页查询用户角色关联
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysUserRole> pageSysUserRoles(SysUserRoleQueryParam query);

    /**
     * 列表查询用户角色关联
     *
     * @param query 查询参数
     * @return 用户角色关联列表
     */
    List<SysUserRole> listSysUserRoles(SysUserRoleQueryParam query);

    /**
     * 更新用户角色关联（全量）。
     *
     * @param sysUserRole 用户角色关联（含主键）
     */
    void updateSysUserRole(SysUserRole sysUserRole);

    /**
     * 按条件更新用户角色关联（只更新传入的非空字段）。
     *
     * @param sysUserRole 用户角色关联（至少含主键）
     */
    void updateByCondition(SysUserRole sysUserRole);

    /**
     * 删除用户角色关联。
     *
     * @param id 用户角色关联 ID
     */
    void deleteSysUserRole(Long id);
}
