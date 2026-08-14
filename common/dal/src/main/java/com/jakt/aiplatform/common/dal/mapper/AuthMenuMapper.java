package com.jakt.aiplatform.common.dal.mapper;

import com.jakt.aiplatform.common.dal.dataobject.AuthMenuDO;
import com.jakt.aiplatform.common.dal.query.AuthMenuDalQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 菜单权限表 Mapper。SQL 全部在 resources/mapper/AuthMenuMapper.xml 中。
 */
@Mapper
public interface AuthMenuMapper {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 菜单权限表数据对象
     */
    AuthMenuDO selectById(Long id);

    /**
     * 查询用户权限码集合（join 角色/菜单，去重、过滤空权限码）。
     *
     * @param userId 用户ID
     * @return 权限码列表
     */
    List<String> selectPermsByUserId(Long userId);

    /**
     * 查询用户可见菜单（M目录/C菜单，join 角色，按父级与顺序排序）。
     *
     * @param userId 用户ID
     * @return 菜单数据列表
     */
    List<AuthMenuDO> selectMenusByUserId(Long userId);

    /**
     * 分页查询：SQL 含 LIMIT #{offset}, #{pageSize}，配合 countByQuery 组装分页结果。
     *
     * @param query 查询参数
     * @return 当前页数据
     */
    List<AuthMenuDO> selectPage(AuthMenuDalQuery query);

    /**
     * 列表查询：与 {@link #selectPage} 完全一致，仅去掉 LIMIT 一行，返回全量结果。
     *
     * @param query 查询参数
     * @return 全量数据
     */
    List<AuthMenuDO> selectList(AuthMenuDalQuery query);

    /**
     * 按查询条件统计总条数，用于分页。
     *
     * @param query 查询参数
     * @return 总条数
     */
    long countByQuery(AuthMenuDalQuery query);

    /**
     * 新增，返回受影响行数；自增主键回填到 {@code authMenuDO.id}。
     *
     * @param authMenuDO 数据对象
     * @return 受影响行数
     */
    int insert(AuthMenuDO authMenuDO);

    /**
     * 按主键更新，返回受影响行数。
     *
     * @param authMenuDO 数据对象
     * @return 受影响行数
     */
    int update(AuthMenuDO authMenuDO);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新），适合只改几个字段的场景。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；create_time/update_time 由数据库自动维护，不参与更新。
     *
     * @param authMenuDO 数据对象（至少含主键）
     * @return 受影响行数
     */
    int updateByCondition(AuthMenuDO authMenuDO);

    /**
     * 按主键删除，返回受影响行数。
     *
     * @param id 主键
     * @return 受影响行数
     */
    int deleteById(Long id);
}
