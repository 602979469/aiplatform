package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.domain.SysRoleDept;
import com.jakt.aiplatform.core.model.param.SysRoleDeptQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 角色部门关联领域服务
 *
 * 实现类为 SysRoleDeptServiceImpl（core.service.impl 包）。
 */
public interface SysRoleDeptService {

    /**
     * 创建角色部门关联
     *
     * @param sysRoleDept 角色部门关联
     * @return 创建后的角色部门关联（主键已回填）
     */
    SysRoleDept createSysRoleDept(SysRoleDept sysRoleDept);

    /**
     * 更新角色部门关联（全量）
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
     * 删除角色部门关联
     *
     * @param id 角色部门关联 ID
     */
    void deleteSysRoleDept(Long id);

    /**
     * 按 ID 获取角色部门关联
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
    PageResult<SysRoleDept> findPage(SysRoleDeptQueryParam query);

    /**
     * 列表查询角色部门关联
     *
     * @param query 查询参数
     * @return 角色部门关联列表
     */
    List<SysRoleDept> findList(SysRoleDeptQueryParam query);
}
