package com.jakt.aiplatform.common.dal.mapper;

import com.jakt.aiplatform.common.dal.dataobject.SysNoticeReadDO;
import com.jakt.aiplatform.core.model.param.SysNoticeReadQueryParam;
import com.jakt.aiplatform.core.model.result.SysNoticeListResult;
import com.jakt.aiplatform.core.model.result.SysReadUserResult;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 公告已读记录 Mapper。SQL 全部在 resources/mapper/SysNoticeReadMapper.xml 中。
 */
@Mapper
public interface SysNoticeReadMapper {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 公告已读记录数据对象
     */
    SysNoticeReadDO selectById(Long id);

    /**
     * 分页查询：SQL 含 LIMIT #{offset}, #{pageSize}，配合 countByQuery 组装分页结果。
     *
     * @param query 查询参数
     * @return 当前页数据
     */
    List<SysNoticeReadDO> selectPage(SysNoticeReadQueryParam query);

    /**
     * 列表查询：与 {@link #selectPage} 完全一致，仅去掉 LIMIT 一行，返回全量结果。
     *
     * @param query 查询参数
     * @return 全量数据
     */
    List<SysNoticeReadDO> selectList(SysNoticeReadQueryParam query);

    /**
     * 按查询条件统计总条数，用于分页。
     *
     * @param query 查询参数
     * @return 总条数
     */
    long countByQuery(SysNoticeReadQueryParam query);

    /**
     * 新增，返回受影响行数；自增主键回填到 {@code sysNoticeReadDO.id}。
     *
     * @param sysNoticeReadDO 数据对象
     * @return 受影响行数
     */
    int insert(SysNoticeReadDO sysNoticeReadDO);

    /**
     * 按主键更新，返回受影响行数。
     *
     * @param sysNoticeReadDO 数据对象
     * @return 受影响行数
     */
    int update(SysNoticeReadDO sysNoticeReadDO);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新），适合只改几个字段的场景。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；create_time/update_time 由数据库自动维护，不参与更新。
     *
     * @param sysNoticeReadDO 数据对象（至少含主键）
     * @return 受影响行数
     */
    int updateByCondition(SysNoticeReadDO sysNoticeReadDO);

    /**
     * 按主键删除，返回受影响行数。
     *
     * @param id 主键
     * @return 受影响行数
     */
    int deleteById(Long id);

    /**
     * 新增已读记录（忽略重复）。
     *
     * @param sysNoticeReadDO 已读记录数据对象
     * @return 影响行数
     */
    int insertNoticeRead(SysNoticeReadDO sysNoticeReadDO);

    /**
     * 查询某用户未读公告数量。
     *
     * @param userId 用户ID
     * @return 未读数量
     */
    int selectUnreadCount(@Param("userId") Long userId);

    /**
     * 查询某用户是否已读某公告。
     *
     * @param noticeId 公告ID
     * @param userId   用户ID
     * @return 已读记录数（0未读 1已读）
     */
    int selectIsRead(@Param("noticeId") Long noticeId, @Param("userId") Long userId);

    /**
     * 批量标记已读。
     *
     * @param userId    用户ID
     * @param noticeIds 公告ID数组
     * @return 影响行数
     */
    int insertNoticeReadBatch(@Param("userId") Long userId, @Param("noticeIds") Long[] noticeIds);

    /**
     * 查询带已读状态的公告列表（SQL 层限制条数）。
     *
     * @param userId 用户ID
     * @param limit  最多返回条数
     * @return 带 isRead 标记的公告列表投影
     */
    List<SysNoticeListResult> selectNoticeListWithReadStatus(@Param("userId") Long userId, @Param("limit") int limit);

    /**
     * 公告删除时清理对应已读记录。
     *
     * @param noticeIds 公告ID数组
     * @return 影响行数
     */
    int deleteByNoticeIds(@Param("noticeIds") String[] noticeIds);

    /**
     * 查询已阅读某公告的用户列表。
     *
     * @param noticeId    公告ID
     * @param searchValue 搜索值
     * @return 已读用户列表投影
     */
    List<SysReadUserResult> selectReadUsersByNoticeId(@Param("noticeId") Long noticeId,
                                                      @Param("searchValue") String searchValue);
}
