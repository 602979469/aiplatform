package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.domain.SysNotice;
import com.jakt.aiplatform.core.model.param.SysNoticeQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 通知公告领域服务
 *
 * 实现类为 SysNoticeServiceImpl（core.service.impl 包）。
 */
public interface SysNoticeService {

    /**
     * 创建通知公告
     *
     * @param sysNotice 通知公告
     * @return 创建后的通知公告（主键已回填）
     */
    SysNotice createSysNotice(SysNotice sysNotice);

    /**
     * 更新通知公告（全量）
     *
     * @param sysNotice 通知公告（含主键）
     */
    void updateSysNotice(SysNotice sysNotice);

    /**
     * 按条件更新通知公告（只更新传入的非空字段）。
     *
     * @param sysNotice 通知公告（至少含主键）
     */
    void updateByCondition(SysNotice sysNotice);

    /**
     * 删除通知公告
     *
     * @param id 通知公告 ID
     */
    void deleteSysNotice(Long id);

    /**
     * 按 ID 获取通知公告
     *
     * @param id 通知公告 ID
     * @return 通知公告
     */
    SysNotice getSysNotice(Long id);

    /**
     * 分页查询通知公告
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysNotice> findPage(SysNoticeQueryParam query);

    /**
     * 列表查询通知公告
     *
     * @param query 查询参数
     * @return 通知公告列表
     */
    List<SysNotice> findList(SysNoticeQueryParam query);
}
