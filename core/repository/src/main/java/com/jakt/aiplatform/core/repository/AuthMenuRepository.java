package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.AuthMenu;
import com.jakt.aiplatform.core.model.param.AuthMenuQueryParam;
import com.jakt.aiplatform.common.util.result.PageResult;

import java.util.List;

/**
 * 菜单权限表仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
public interface AuthMenuRepository {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 菜单权限表领域模型
     */
    AuthMenu findById(Long id);

    /**
     * 分页查询。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<AuthMenu> findPage(AuthMenuQueryParam query);

    /**
     * 列表查询。
     *
     * @param query 查询参数
     * @return 菜单权限表列表
     */
    List<AuthMenu> findList(AuthMenuQueryParam query);

    /**
     * 按条件查询单条：基于 {@code findList} 的结果集判断，不新增 Mapper 方法。
     *
     * @param query 查询参数
     * @return 菜单权限表领域模型；未查询到返回 null，结果多于 1 条抛「查询结果不唯一」
     */
    AuthMenu findOne(AuthMenuQueryParam query);

    /**
     * 新增。
     *
     * @param authMenu 菜单权限表
     * @return 新增后的菜单权限表；主键已回填到入参，返回同一对象
     */
    AuthMenu insert(AuthMenu authMenu);

    /**
     * 更新。
     *
     * @param authMenu 菜单权限表
     */
    int update(AuthMenu authMenu);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新）。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；create_time/update_time 由数据库自动维护。
     *
     * @param authMenu 菜单权限表（至少含主键）
     */
    int updateByCondition(AuthMenu authMenu);

    /**
     * 按主键删除。
     *
     * @param id 主键
     */
    int deleteById(Long id);

    /**
     * 查询用户可见菜单（M目录/C菜单，按父级与顺序排序）。
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<AuthMenu> findMenusByUserId(Long userId);

    /**
     * 查询用户权限码集合。
     *
     * @param userId 用户ID
     * @return 权限码列表
     */
    List<String> findPermsByUserId(Long userId);

    /**
     * 清空菜单关联（菜单删除时调用：角色菜单）。
     *
     * @param menuId 菜单ID
     */
    void clearMenuBindings(Long menuId);
}
