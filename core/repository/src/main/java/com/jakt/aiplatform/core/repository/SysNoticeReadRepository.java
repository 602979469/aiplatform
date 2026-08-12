package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysNoticeRead;
import com.jakt.aiplatform.core.model.param.SysNoticeReadQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 公告已读记录仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
public interface SysNoticeReadRepository {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 公告已读记录领域模型
     */
    SysNoticeRead findById(Long id);

    /**
     * 分页查询。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysNoticeRead> findPage(SysNoticeReadQueryParam query);

    /**
     * 列表查询。
     *
     * @param query 查询参数
     * @return 公告已读记录列表
     */
    List<SysNoticeRead> findList(SysNoticeReadQueryParam query);

    /**
     * 新增。
     *
     * @param sysNoticeRead 公告已读记录
     * @return 新增后的公告已读记录（主键已回填）
     */
    SysNoticeRead insert(SysNoticeRead sysNoticeRead);

    /**
     * 更新。
     *
     * @param sysNoticeRead 公告已读记录
     */
    void update(SysNoticeRead sysNoticeRead);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新）。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；create_time/update_time 由数据库自动维护。
     *
     * @param sysNoticeRead 公告已读记录（至少含主键）
     */
    void updateByCondition(SysNoticeRead sysNoticeRead);

    /**
     * 按主键删除。
     *
     * @param id 主键
     */
    void deleteById(Long id);
}
