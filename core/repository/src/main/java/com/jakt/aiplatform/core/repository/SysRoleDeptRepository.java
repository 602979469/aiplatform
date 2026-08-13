package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysRoleDept;

import java.util.List;

/**
 * 角色部门关联仓储（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
public interface SysRoleDeptRepository {

    /**
     * 按角色ID删除关联。
     *
     * @param roleId 角色ID
     * @return 影响行数
     */
    int deleteRoleDeptByRoleId(Long roleId);

    /**
     * 按部门ID统计关联数量。
     *
     * @param deptId 部门ID
     * @return 关联数量
     */
    int selectCountRoleDeptByDeptId(Long deptId);

    /**
     * 按角色ID集合批量删除关联。
     *
     * @param ids 角色ID数组
     * @return 影响行数
     */
    int deleteRoleDept(Long[] ids);

    /**
     * 批量新增角色部门关联。
     *
     * @param roleDeptList 角色部门关联列表
     * @return 影响行数
     */
    int batchRoleDept(List<SysRoleDept> roleDeptList);
}
