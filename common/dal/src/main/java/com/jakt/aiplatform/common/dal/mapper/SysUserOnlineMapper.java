package com.jakt.aiplatform.common.dal.mapper;

import com.jakt.aiplatform.common.dal.dataobject.SysUserOnlineDO;
import com.jakt.aiplatform.core.model.param.SysUserOnlineQueryParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 在线用户 Mapper。SQL 全部在 resources/mapper/SysUserOnlineMapper.xml 中。
 */
@Mapper
public interface SysUserOnlineMapper {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 在线用户数据对象
     */
    SysUserOnlineDO selectById(String id);

    /**
     * 分页查询：SQL 含 LIMIT #{offset}, #{pageSize}，配合 countByQuery 组装分页结果。
     *
     * @param query 查询参数
     * @return 当前页数据
     */
    List<SysUserOnlineDO> selectPage(SysUserOnlineQueryParam query);

    /**
     * 列表查询：与 {@link #selectPage} 完全一致，仅去掉 LIMIT 一行，返回全量结果。
     *
     * @param query 查询参数
     * @return 全量数据
     */
    List<SysUserOnlineDO> selectList(SysUserOnlineQueryParam query);

    /**
     * 按查询条件统计总条数，用于分页。
     *
     * @param query 查询参数
     * @return 总条数
     */
    long countByQuery(SysUserOnlineQueryParam query);

    /**
     * 新增，返回受影响行数；自增主键回填到 {@code sysUserOnlineDO.id}。
     *
     * @param sysUserOnlineDO 数据对象
     * @return 受影响行数
     */
    int insert(SysUserOnlineDO sysUserOnlineDO);

    /**
     * 按主键更新，返回受影响行数。
     *
     * @param sysUserOnlineDO 数据对象
     * @return 受影响行数
     */
    int update(SysUserOnlineDO sysUserOnlineDO);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新），适合只改几个字段的场景。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；create_time/update_time 由数据库自动维护，不参与更新。
     *
     * @param sysUserOnlineDO 数据对象（至少含主键）
     * @return 受影响行数
     */
    int updateByCondition(SysUserOnlineDO sysUserOnlineDO);

    /**
     * 按主键删除，返回受影响行数。
     *
     * @param id 主键
     * @return 受影响行数
     */
    int deleteById(String id);

    /**
     * 新增/覆盖在线用户（replace into）。
     *
     * @param sysUserOnlineDO 数据对象
     * @return 影响行数
     */
    int saveOnline(SysUserOnlineDO sysUserOnlineDO);

    /**
     * 按条件查询在线用户列表（login_name/ipaddr 模糊匹配）。
     *
     * @param query 查询参数
     * @return 在线用户数据对象列表
     */
    List<SysUserOnlineDO> selectUserOnlineList(SysUserOnlineQueryParam query);

    /**
     * 查询最后访问时间早于指定时间的在线用户（按最后访问时间升序）。
     *
     * @param lastAccessTime 最后访问时间
     * @return 在线用户数据对象列表
     */
    List<SysUserOnlineDO> selectOnlineByExpired(String lastAccessTime);
}
