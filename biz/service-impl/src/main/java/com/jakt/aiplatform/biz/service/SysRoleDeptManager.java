package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.core.model.domain.SysRoleDept;
import com.jakt.aiplatform.core.model.param.SysRoleDeptQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 角色部门关联管理类接口定义
 * 
 */
public interface SysRoleDeptManager {

    /**
     * 创建角色部门关联
     *
     * @param sysRoleDept 角色部门关联
     * @return 创建成功后的角色部门关联
     */
    SysRoleDept createSysRoleDept(SysRoleDept sysRoleDept);

    /**
     * 按 ID 查询角色部门关联
     *
     * @param id 角色部门关联 ID
     * @return 角色部门关联
     */
    SysRoleDept getSysRoleDept(Long id);

    /**
     * 分页查询角色部门关联
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysRoleDept> pageSysRoleDepts(SysRoleDeptQueryParam query);

    /**
     * 列表查询角色部门关联
     *
     * @param query 查询参数
     * @return 角色部门关联列表
     */
    List<SysRoleDept> listSysRoleDepts(SysRoleDeptQueryParam query);

    /**
     * 更新角色部门关联（全量）。
     *
     * @param sysRoleDept 角色部门关联（含主键）
     */
    void updateSysRoleDept(SysRoleDept sysRoleDept);

    /**
     * 按条件更新角色部门关联（只更新传入的非空字段）。
     *
     * @param sysRoleDept 角色部门关联（至少含主键）
     */
    void updateByCondition(SysRoleDept sysRoleDept);

    /**
     * 删除角色部门关联。
     *
     * @param id 角色部门关联 ID
     */
    void deleteSysRoleDept(Long id);
}
