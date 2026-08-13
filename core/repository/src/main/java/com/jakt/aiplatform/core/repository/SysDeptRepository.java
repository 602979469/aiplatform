package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysDept;

import java.util.List;

/**
 * 部门仓储（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
public interface SysDeptRepository {

    /**
     * 按条件统计部门数量。
     *
     * @param dept 查询条件
     * @return 部门数量
     */
    int selectDeptCount(SysDept dept);

    /**
     * 校验部门下是否存在用户。
     *
     * @param deptId 部门ID
     * @return 用户数量
     */
    int checkDeptExistUser(Long deptId);

    /**
     * 按条件查询部门列表。
     *
     * @param dept 查询条件（实体即条件）
     * @return 部门列表
     */
    List<SysDept> selectDeptList(SysDept dept);

    /**
     * 按部门ID删除部门（逻辑删除）。
     *
     * @param deptId 部门ID
     * @return 影响行数
     */
    int deleteDeptById(Long deptId);

    /**
     * 新增部门。
     *
     * @param dept 部门
     * @return 影响行数
     */
    int insertDept(SysDept dept);

    /**
     * 全量更新部门。
     *
     * @param dept 部门
     * @return 影响行数
     */
    int updateDept(SysDept dept);

    /**
     * 批量更新部门祖级列表。
     *
     * @param depts 部门列表
     * @return 影响行数
     */
    int updateDeptChildren(List<SysDept> depts);

    /**
     * 按部门ID查询部门（含父部门名称）。
     *
     * @param deptId 部门ID
     * @return 部门领域模型
     */
    SysDept selectDeptById(Long deptId);

    /**
     * 校验部门名称在同级下唯一。
     *
     * @param dept 部门（deptName + parentId，deptId 用于排除自身）
     * @return 是否唯一
     */
    boolean checkDeptNameUnique(SysDept dept);

    /**
     * 按角色ID查询部门树标识集合。
     *
     * @param roleId 角色ID
     * @return 部门树标识集合
     */
    List<String> selectRoleDeptTree(Long roleId);

    /**
     * 批量恢复部门状态为正常。
     *
     * @param deptIds 部门ID数组
     * @return 影响行数
     */
    int updateDeptStatusNormal(Long[] deptIds);

    /**
     * 查询某部门全部子部门。
     *
     * @param deptId 部门ID
     * @return 子部门列表
     */
    List<SysDept> selectChildrenDeptById(Long deptId);

    /**
     * 查询某部门正常状态的子部门数量。
     *
     * @param deptId 部门ID
     * @return 子部门数量
     */
    int selectNormalChildrenDeptById(Long deptId);

    /**
     * 仅更新部门排序。
     *
     * @param dept 部门（deptId + orderNum）
     * @return 影响行数
     */
    int updateDeptSort(SysDept dept);
}
