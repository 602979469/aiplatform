package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysNotice;
import com.jakt.aiplatform.core.model.domain.SysNoticeRead;
import com.jakt.aiplatform.core.model.result.SysReadUserResult;

import java.util.List;

/**
 * 公告已读记录仓储（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
public interface SysNoticeReadRepository {

    /**
     * 新增已读记录（忽略重复）。
     *
     * @param noticeRead 已读记录
     * @return 影响行数
     */
    int insertNoticeRead(SysNoticeRead noticeRead);

    /**
     * 查询某用户未读公告数量。
     *
     * @param userId 用户ID
     * @return 未读数量
     */
    int selectUnreadCount(Long userId);

    /**
     * 查询某用户是否已读某公告。
     *
     * @param noticeId 公告ID
     * @param userId   用户ID
     * @return 已读记录数（0未读 1已读）
     */
    int selectIsRead(Long noticeId, Long userId);

    /**
     * 批量标记已读。
     *
     * @param userId    用户ID
     * @param noticeIds 公告ID数组
     * @return 影响行数
     */
    int insertNoticeReadBatch(Long userId, Long[] noticeIds);

    /**
     * 查询带已读状态的公告列表（SQL 层限制条数）。
     *
     * @param userId 用户ID
     * @param limit  最多返回条数
     * @return 公告列表（含 isRead 标记）
     */
    List<SysNotice> selectNoticeListWithReadStatus(Long userId, int limit);

    /**
     * 公告删除时清理对应已读记录。
     *
     * @param noticeIds 公告ID数组
     * @return 影响行数
     */
    int deleteByNoticeIds(String[] noticeIds);

    /**
     * 查询已阅读某公告的用户列表。
     *
     * @param noticeId    公告ID
     * @param searchValue 搜索值
     * @return 已读用户列表
     */
    List<SysReadUserResult> selectReadUsersByNoticeId(Long noticeId, String searchValue);
}
