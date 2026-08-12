package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.domain.SysNoticeRead;
import com.jakt.aiplatform.core.model.param.SysNoticeReadQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 公告已读记录领域服务
 *
 * 实现类为 SysNoticeReadServiceImpl（core.service.impl 包）。
 */
public interface SysNoticeReadService {

    /**
     * 创建公告已读记录
     *
     * @param sysNoticeRead 公告已读记录
     * @return 创建后的公告已读记录（主键已回填）
     */
    SysNoticeRead createSysNoticeRead(SysNoticeRead sysNoticeRead);

    /**
     * 更新公告已读记录（全量）
     *
     * @param sysNoticeRead 公告已读记录（含主键）
     */
    void updateSysNoticeRead(SysNoticeRead sysNoticeRead);

    /**
     * 按条件更新公告已读记录（只更新传入的非空字段）。
     *
     * @param sysNoticeRead 公告已读记录（至少含主键）
     */
    void updateByCondition(SysNoticeRead sysNoticeRead);

    /**
     * 删除公告已读记录
     *
     * @param id 公告已读记录 ID
     */
    void deleteSysNoticeRead(Long id);

    /**
     * 按 ID 获取公告已读记录
     *
     * @param id 公告已读记录 ID
     * @return 公告已读记录
     */
    SysNoticeRead getSysNoticeRead(Long id);

    /**
     * 分页查询公告已读记录
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysNoticeRead> findPage(SysNoticeReadQueryParam query);

    /**
     * 列表查询公告已读记录
     *
     * @param query 查询参数
     * @return 公告已读记录列表
     */
    List<SysNoticeRead> findList(SysNoticeReadQueryParam query);
}
