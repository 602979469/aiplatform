package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.core.model.domain.SysPost;
import com.jakt.aiplatform.core.model.param.SysPostQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 岗位管理类接口定义
 * 
 */
public interface SysPostManager {

    /**
     * 创建岗位
     *
     * @param sysPost 岗位
     * @return 创建成功后的岗位
     */
    SysPost createSysPost(SysPost sysPost);

    /**
     * 按 ID 查询岗位
     *
     * @param id 岗位 ID
     * @return 岗位
     */
    SysPost getSysPost(Long id);

    /**
     * 分页查询岗位
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysPost> pageSysPosts(SysPostQueryParam query);

    /**
     * 列表查询岗位
     *
     * @param query 查询参数
     * @return 岗位列表
     */
    List<SysPost> listSysPosts(SysPostQueryParam query);

    /**
     * 更新岗位（全量）。
     *
     * @param sysPost 岗位（含主键）
     */
    void updateSysPost(SysPost sysPost);

    /**
     * 按条件更新岗位（只更新传入的非空字段）。
     *
     * @param sysPost 岗位（至少含主键）
     */
    void updateByCondition(SysPost sysPost);

    /**
     * 删除岗位。
     *
     * @param id 岗位 ID
     */
    void deleteSysPost(Long id);
}
