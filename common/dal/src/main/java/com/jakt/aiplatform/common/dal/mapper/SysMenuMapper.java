package com.jakt.aiplatform.common.dal.mapper;

import com.jakt.aiplatform.common.dal.dataobject.SysMenuDO;
import com.jakt.aiplatform.core.model.param.SysMenuQueryParam;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 菜单 Mapper。SQL 全部在 resources/mapper/SysMenuMapper.xml 中。
 */
@Mapper
public interface SysMenuMapper {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 菜单数据对象
     */
    SysMenuDO selectById(Long id);

    /**
     * 分页查询：SQL 含 LIMIT #{offset}, #{pageSize}，配合 countByQuery 组装分页结果。
     *
     * @param query 查询参数
     * @return 当前页数据
     */
    List<SysMenuDO> selectPage(SysMenuQueryParam query);

    /**
     * 列表查询：与 {@link #selectPage} 完全一致，仅去掉 LIMIT 一行，返回全量结果。
     *
     * @param query 查询参数
     * @return 全量数据
     */
    List<SysMenuDO> selectList(SysMenuQueryParam query);

    /**
     * 按查询条件统计总条数，用于分页。
     *
     * @param query 查询参数
     * @return 总条数
     */
    long countByQuery(SysMenuQueryParam query);

    /**
     * 新增，返回受影响行数；自增主键回填到 {@code sysMenuDO.id}。
     *
     * @param sysMenuDO 数据对象
     * @return 受影响行数
     */
    int insert(SysMenuDO sysMenuDO);

    /**
     * 按主键更新，返回受影响行数。
     *
     * @param sysMenuDO 数据对象
     * @return 受影响行数
     */
    int update(SysMenuDO sysMenuDO);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新），适合只改几个字段的场景。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；create_time/update_time 由数据库自动维护，不参与更新。
     *
     * @param sysMenuDO 数据对象（至少含主键）
     * @return 受影响行数
     */
    int updateByCondition(SysMenuDO sysMenuDO);

    /**
     * 按主键删除，返回受影响行数。
     *
     * @param id 主键
     * @return 受影响行数
     */
    int deleteById(Long id);

    /**
     * 查询全部菜单（按 parent_id、order_num 排序）。
     *
     * @return 菜单数据对象列表
     */
    List<SysMenuDO> selectMenuAll();

    /**
     * 按用户ID查询全部菜单（join 角色）。
     *
     * @param userId 用户ID
     * @return 菜单数据对象列表
     */
    List<SysMenuDO> selectMenuAllByUserId(Long userId);

    /**
     * 查询正常状态菜单（菜单/按钮 + visible=0）。
     *
     * @return 菜单数据对象列表
     */
    List<SysMenuDO> selectMenuNormalAll();

    /**
     * 按用户ID查询可用菜单（菜单/按钮 + visible=0 + 角色正常）。
     *
     * @param userId 用户ID
     * @return 菜单数据对象列表
     */
    List<SysMenuDO> selectMenusByUserId(Long userId);

    /**
     * 按用户ID查询权限标识集合。
     *
     * @param userId 用户ID
     * @return 权限标识集合
     */
    List<String> selectPermsByUserId(Long userId);

    /**
     * 按角色ID查询权限标识集合。
     *
     * @param roleId 角色ID
     * @return 权限标识集合
     */
    List<String> selectPermsByRoleId(Long roleId);

    /**
     * 按角色ID查询菜单树标识集合。
     *
     * @param roleId 角色ID
     * @return 菜单树标识集合
     */
    List<String> selectMenuTree(Long roleId);

    /**
     * 按条件查询菜单列表（menu_name 模糊 + visible）。
     *
     * @param query 查询参数
     * @return 菜单数据对象列表
     */
    List<SysMenuDO> selectMenuList(SysMenuQueryParam query);

    /**
     * 按用户ID与条件查询菜单列表。
     *
     * @param query 查询参数（userId 放 params）
     * @return 菜单数据对象列表
     */
    List<SysMenuDO> selectMenuListByUserId(SysMenuQueryParam query);

    /**
     * 按菜单ID删除菜单及其子菜单。
     *
     * @param menuId 菜单ID
     * @return 影响行数
     */
    int deleteMenuById(Long menuId);

    /**
     * 按菜单ID查询菜单（含父菜单名称）。
     *
     * @param menuId 菜单ID
     * @return 菜单数据对象
     */
    SysMenuDO selectMenuById(Long menuId);

    /**
     * 按父菜单ID统计子菜单数量。
     *
     * @param parentId 父菜单ID
     * @return 子菜单数量
     */
    int selectCountMenuByParentId(Long parentId);

    /**
     * 仅更新菜单排序。
     *
     * @param sysMenuDO 菜单数据对象（menuId + orderNum）
     * @return 影响行数
     */
    int updateMenuSort(SysMenuDO sysMenuDO);

    /**
     * 校验菜单名称在同级下唯一。
     *
     * @param menuName 菜单名称
     * @param parentId 父菜单ID
     * @return 菜单数据对象（limit 1）
     */
    SysMenuDO checkMenuNameUnique(@Param("menuName") String menuName, @Param("parentId") Long parentId);
}
