package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.core.model.domain.SysRole;
import com.jakt.aiplatform.core.model.param.SysRoleQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 角色管理类接口定义
 * 
 */
public interface SysRoleManager {

    /**
     * 创建角色
     *
     * @param sysRole 角色
     * @return 创建成功后的角色
     */
    SysRole createSysRole(SysRole sysRole);

    /**
     * 按 ID 查询角色
     *
     * @param id 角色 ID
     * @return 角色
     */
    SysRole getSysRole(Long id);

    /**
     * 分页查询角色
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysRole> pageSysRoles(SysRoleQueryParam query);

    /**
     * 列表查询角色
     *
     * @param query 查询参数
     * @return 角色列表
     */
    List<SysRole> listSysRoles(SysRoleQueryParam query);

    /**
     * 更新角色（全量）。
     *
     * @param sysRole 角色（含主键）
     */
    void updateSysRole(SysRole sysRole);

    /**
     * 按条件更新角色（只更新传入的非空字段）。
     *
     * @param sysRole 角色（至少含主键）
     */
    void updateByCondition(SysRole sysRole);

    /**
     * 删除角色。
     *
     * @param id 角色 ID
     */
    void deleteSysRole(Long id);
}
