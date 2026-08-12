package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.domain.SysUserOnline;
import com.jakt.aiplatform.core.model.param.SysUserOnlineQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 在线用户领域服务
 *
 * 实现类为 SysUserOnlineServiceImpl（core.service.impl 包）。
 */
public interface SysUserOnlineService {

    /**
     * 创建在线用户
     *
     * @param sysUserOnline 在线用户
     * @return 创建后的在线用户（主键已回填）
     */
    SysUserOnline createSysUserOnline(SysUserOnline sysUserOnline);

    /**
     * 更新在线用户（全量）
     *
     * @param sysUserOnline 在线用户（含主键）
     */
    void updateSysUserOnline(SysUserOnline sysUserOnline);

    /**
     * 按条件更新在线用户（只更新传入的非空字段）。
     *
     * @param sysUserOnline 在线用户（至少含主键）
     */
    void updateByCondition(SysUserOnline sysUserOnline);

    /**
     * 删除在线用户
     *
     * @param id 在线用户 ID
     */
    void deleteSysUserOnline(String id);

    /**
     * 按 ID 获取在线用户
     *
     * @param id 在线用户 ID
     * @return 在线用户
     */
    SysUserOnline getSysUserOnline(String id);

    /**
     * 分页查询在线用户
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysUserOnline> findPage(SysUserOnlineQueryParam query);

    /**
     * 列表查询在线用户
     *
     * @param query 查询参数
     * @return 在线用户列表
     */
    List<SysUserOnline> findList(SysUserOnlineQueryParam query);
}
