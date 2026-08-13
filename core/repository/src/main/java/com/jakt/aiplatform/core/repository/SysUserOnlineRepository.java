package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysUserOnline;

import java.util.List;

/**
 * 在线用户仓储（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
public interface SysUserOnlineRepository {

    /**
     * 按会话ID查询在线用户。
     *
     * @param sessionId 会话ID
     * @return 在线用户领域模型
     */
    SysUserOnline selectOnlineById(String sessionId);

    /**
     * 按会话ID删除在线用户。
     *
     * @param sessionId 会话ID
     * @return 影响行数
     */
    int deleteOnlineById(String sessionId);

    /**
     * 新增/覆盖在线用户。
     *
     * @param online 在线用户
     * @return 影响行数
     */
    int saveOnline(SysUserOnline online);

    /**
     * 按条件查询在线用户列表。
     *
     * @param userOnline 查询条件（实体即条件）
     * @return 在线用户列表
     */
    List<SysUserOnline> selectUserOnlineList(SysUserOnline userOnline);

    /**
     * 查询最后访问时间早于指定时间的在线用户。
     *
     * @param lastAccessTime 最后访问时间
     * @return 在线用户列表
     */
    List<SysUserOnline> selectOnlineByExpired(String lastAccessTime);
}
