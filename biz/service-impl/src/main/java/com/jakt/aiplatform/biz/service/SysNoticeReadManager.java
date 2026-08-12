package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.core.model.domain.SysNoticeRead;
import com.jakt.aiplatform.core.model.param.SysNoticeReadQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 公告已读记录管理类接口定义
 * 
 */
public interface SysNoticeReadManager {

    /**
     * 创建公告已读记录
     *
     * @param sysNoticeRead 公告已读记录
     * @return 创建成功后的公告已读记录
     */
    SysNoticeRead createSysNoticeRead(SysNoticeRead sysNoticeRead);

    /**
     * 按 ID 查询公告已读记录
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
    PageResult<SysNoticeRead> pageSysNoticeReads(SysNoticeReadQueryParam query);

    /**
     * 列表查询公告已读记录
     *
     * @param query 查询参数
     * @return 公告已读记录列表
     */
    List<SysNoticeRead> listSysNoticeReads(SysNoticeReadQueryParam query);

    /**
     * 更新公告已读记录（全量）。
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
     * 删除公告已读记录。
     *
     * @param id 公告已读记录 ID
     */
    void deleteSysNoticeRead(Long id);
}
