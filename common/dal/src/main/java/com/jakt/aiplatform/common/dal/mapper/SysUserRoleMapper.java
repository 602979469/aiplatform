package com.jakt.aiplatform.common.dal.mapper;

import com.jakt.aiplatform.common.dal.dataobject.SysUserRoleDO;
import com.jakt.aiplatform.core.model.param.SysUserRoleQueryParam;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户角色关联 Mapper。SQL 全部在 resources/mapper/SysUserRoleMapper.xml 中。
 */
@Mapper
public interface SysUserRoleMapper {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 用户角色关联数据对象
     */
    SysUserRoleDO selectById(Long id);

    /**
     * 分页查询：SQL 含 LIMIT #{offset}, #{pageSize}，配合 countByQuery 组装分页结果。
     *
     * @param query 查询参数
     * @return 当前页数据
     */
    List<SysUserRoleDO> selectPage(SysUserRoleQueryParam query);

    /**
     * 列表查询：与 {@link #selectPage} 完全一致，仅去掉 LIMIT 一行，返回全量结果。
     *
     * @param query 查询参数
     * @return 全量数据
     */
    List<SysUserRoleDO> selectList(SysUserRoleQueryParam query);

    /**
     * 按查询条件统计总条数，用于分页。
     *
     * @param query 查询参数
     * @return 总条数
     */
    long countByQuery(SysUserRoleQueryParam query);

    /**
     * 新增，返回受影响行数；自增主键回填到 {@code sysUserRoleDO.id}。
     *
     * @param sysUserRoleDO 数据对象
     * @return 受影响行数
     */
    int insert(SysUserRoleDO sysUserRoleDO);

    /**
     * 按主键更新，返回受影响行数。
     *
     * @param sysUserRoleDO 数据对象
     * @return 受影响行数
     */
    int update(SysUserRoleDO sysUserRoleDO);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新），适合只改几个字段的场景。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；create_time/update_time 由数据库自动维护，不参与更新。
     *
     * @param sysUserRoleDO 数据对象（至少含主键）
     * @return 受影响行数
     */
    int updateByCondition(SysUserRoleDO sysUserRoleDO);

    /**
     * 按主键删除，返回受影响行数。
     *
     * @param id 主键
     * @return 受影响行数
     */
    int deleteById(Long id);

    /**
     * 按用户ID查询关联列表。
     *
     * @param userId 用户ID
     * @return 用户角色关联数据对象列表
     */
    List<SysUserRoleDO> selectUserRoleByUserId(Long userId);

    /**
     * 按用户ID删除关联。
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteUserRoleByUserId(Long userId);

    /**
     * 按用户ID集合批量删除关联。
     *
     * @param ids 用户ID数组
     * @return 影响行数
     */
    int deleteUserRole(Long[] ids);

    /**
     * 按角色ID统计关联数量。
     *
     * @param roleId 角色ID
     * @return 关联数量
     */
    int countUserRoleByRoleId(Long roleId);

    /**
     * 批量新增用户角色关联。
     *
     * @param userRoleList 关联数据对象列表
     * @return 影响行数
     */
    int batchUserRole(List<SysUserRoleDO> userRoleList);

    /**
     * 按用户ID与角色ID删除单条关联。
     *
     * @param userRole 关联数据对象（user_id + role_id）
     * @return 影响行数
     */
    int deleteUserRoleInfo(SysUserRoleDO userRole);

    /**
     * 按角色ID与用户ID集合批量删除关联。
     *
     * @param roleId  角色ID
     * @param userIds 用户ID数组
     * @return 影响行数
     */
    int deleteUserRoleInfos(@Param("roleId") Long roleId, @Param("userIds") Long[] userIds);
}
