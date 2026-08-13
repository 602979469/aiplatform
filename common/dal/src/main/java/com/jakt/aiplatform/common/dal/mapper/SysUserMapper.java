package com.jakt.aiplatform.common.dal.mapper;

import com.jakt.aiplatform.common.dal.dataobject.SysUserDO;
import com.jakt.aiplatform.core.model.result.SysUserDetailResult;
import com.jakt.aiplatform.core.model.result.SysUserListResult;
import com.jakt.aiplatform.core.model.param.SysUserQueryParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户 Mapper。SQL 全部在 resources/mapper/SysUserMapper.xml 中。
 */
@Mapper
public interface SysUserMapper {

    /**
     * 用户列表（join 部门，返回投影结果）。
     *
     * @param query 查询条件
     * @return 用户投影列表
     */
    List<SysUserListResult> selectUserList(SysUserQueryParam query);

    /**
     * 已分配指定角色的用户列表（join 部门）。
     *
     * @param query 查询条件（roleId 必填）
     * @return 用户投影列表
     */
    List<SysUserListResult> selectAllocatedList(SysUserQueryParam query);

    /**
     * 未分配指定角色的用户列表（join 部门）。
     *
     * @param query 查询条件（roleId 必填）
     * @return 用户投影列表
     */
    List<SysUserListResult> selectUnallocatedList(SysUserQueryParam query);

    /**
     * 用户详情（selectUserVo 语义：join 部门 + 角色，一行 = 用户 × 一个角色）。
     *
     * @param query 查询条件（userId/loginName/phonenumber/email）
     * @return 用户详情投影行
     */
    List<SysUserDetailResult> selectUserDetail(SysUserQueryParam query);

    /**
     * 按 ID 集合批量删除（逻辑删除）。
     *
     * @param ids 用户ID数组
     * @return 影响行数
     */
    int deleteByIds(Long[] ids);

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 用户数据对象
     */
    SysUserDO selectById(Long id);

    /**
     * 分页查询：SQL 含 LIMIT #{offset}, #{pageSize}，配合 countByQuery 组装分页结果。
     *
     * @param query 查询参数
     * @return 当前页数据
     */
    List<SysUserDO> selectPage(SysUserQueryParam query);

    /**
     * 列表查询：与 {@link #selectPage} 完全一致，仅去掉 LIMIT 一行，返回全量结果。
     *
     * @param query 查询参数
     * @return 全量数据
     */
    List<SysUserDO> selectList(SysUserQueryParam query);

    /**
     * 按查询条件统计总条数，用于分页。
     *
     * @param query 查询参数
     * @return 总条数
     */
    long countByQuery(SysUserQueryParam query);

    /**
     * 新增，返回受影响行数；自增主键回填到 {@code sysUserDO.id}。
     *
     * @param sysUserDO 数据对象
     * @return 受影响行数
     */
    int insert(SysUserDO sysUserDO);

    /**
     * 按主键更新，返回受影响行数。
     *
     * @param sysUserDO 数据对象
     * @return 受影响行数
     */
    int update(SysUserDO sysUserDO);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新），适合只改几个字段的场景。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；create_time/update_time 由数据库自动维护，不参与更新。
     *
     * @param sysUserDO 数据对象（至少含主键）
     * @return 受影响行数
     */
    int updateByCondition(SysUserDO sysUserDO);

    /**
     * 按主键删除，返回受影响行数。
     *
     * @param id 主键
     * @return 受影响行数
     */
    int deleteById(Long id);
}
