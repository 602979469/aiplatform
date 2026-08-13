package com.jakt.aiplatform.common.dal.mapper;

import com.jakt.aiplatform.common.dal.dataobject.SysDeptDO;
import com.jakt.aiplatform.core.model.param.SysDeptQueryParam;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 部门 Mapper。SQL 全部在 resources/mapper/SysDeptMapper.xml 中。
 */
@Mapper
public interface SysDeptMapper {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 部门数据对象
     */
    SysDeptDO selectById(Long id);

    /**
     * 分页查询：SQL 含 LIMIT #{offset}, #{pageSize}，配合 countByQuery 组装分页结果。
     *
     * @param query 查询参数
     * @return 当前页数据
     */
    List<SysDeptDO> selectPage(SysDeptQueryParam query);

    /**
     * 列表查询：与 {@link #selectPage} 完全一致，仅去掉 LIMIT 一行，返回全量结果。
     *
     * @param query 查询参数
     * @return 全量数据
     */
    List<SysDeptDO> selectList(SysDeptQueryParam query);

    /**
     * 按查询条件统计总条数，用于分页。
     *
     * @param query 查询参数
     * @return 总条数
     */
    long countByQuery(SysDeptQueryParam query);

    /**
     * 新增，返回受影响行数；自增主键回填到 {@code sysDeptDO.id}。
     *
     * @param sysDeptDO 数据对象
     * @return 受影响行数
     */
    int insert(SysDeptDO sysDeptDO);

    /**
     * 按主键更新，返回受影响行数。
     *
     * @param sysDeptDO 数据对象
     * @return 受影响行数
     */
    int update(SysDeptDO sysDeptDO);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新），适合只改几个字段的场景。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；create_time/update_time 由数据库自动维护，不参与更新。
     *
     * @param sysDeptDO 数据对象（至少含主键）
     * @return 受影响行数
     */
    int updateByCondition(SysDeptDO sysDeptDO);

    /**
     * 按主键删除，返回受影响行数。
     *
     * @param id 主键
     * @return 受影响行数
     */
    int deleteById(Long id);

    /**
     * 按条件统计部门数量。
     *
     * @param query 查询参数
     * @return 部门数量
     */
    int selectDeptCount(SysDeptQueryParam query);

    /**
     * 校验部门下是否存在用户。
     *
     * @param deptId 部门ID
     * @return 用户数量
     */
    int checkDeptExistUser(Long deptId);

    /**
     * 按条件查询部门列表（dept_name 模糊，按 parent_id、order_num 排序）。
     *
     * @param query 查询参数
     * @return 部门数据对象列表
     */
    List<SysDeptDO> selectDeptList(SysDeptQueryParam query);

    /**
     * 批量更新部门祖级列表。
     *
     * @param depts 部门数据对象列表
     * @return 影响行数
     */
    int updateDeptChildren(List<SysDeptDO> depts);

    /**
     * 按部门ID查询部门（含父部门名称）。
     *
     * @param deptId 部门ID
     * @return 部门数据对象
     */
    SysDeptDO selectDeptById(Long deptId);

    /**
     * 校验部门名称在同级下唯一。
     *
     * @param deptName 部门名称
     * @param parentId 父部门ID
     * @return 部门数据对象（limit 1）
     */
    SysDeptDO checkDeptNameUnique(@Param("deptName") String deptName, @Param("parentId") Long parentId);

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
     * @return 部门数据对象列表
     */
    List<SysDeptDO> selectChildrenDeptById(Long deptId);

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
     * @param sysDeptDO 部门数据对象（deptId + orderNum）
     * @return 影响行数
     */
    int updateDeptSort(SysDeptDO sysDeptDO);
}
