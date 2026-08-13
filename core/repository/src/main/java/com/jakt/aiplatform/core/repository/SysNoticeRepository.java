package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysNotice;

import java.util.List;

/**
 * 通知公告仓储（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
public interface SysNoticeRepository {

    /**
     * 查询公告列表。
     *
     * @param notice 查询条件
     * @return 公告列表
     */
    List<SysNotice> selectNoticeList(SysNotice notice);

    /**
     * 按主键查询公告。
     *
     * @param noticeId 公告ID
     * @return 通知公告领域模型
     */
    SysNotice selectNoticeById(Long noticeId);

    /**
     * 新增公告。
     *
     * @param notice 公告
     * @return 影响行数
     */
    int insertNotice(SysNotice notice);

    /**
     * 全量更新公告。
     *
     * @param notice 公告
     * @return 影响行数
     */
    int updateNotice(SysNotice notice);

    /**
     * 按 ID 集合批量删除。
     *
     * @param ids 公告ID集合（逗号分隔）
     * @return 影响行数
     */
    int deleteNoticeByIds(String ids);
}
