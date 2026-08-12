package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.core.model.domain.SysLogininfor;
import com.jakt.aiplatform.core.model.param.SysLogininforQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 登录日志管理类接口定义
 * 
 */
public interface SysLogininforManager {

    /**
     * 创建登录日志
     *
     * @param sysLogininfor 登录日志
     * @return 创建成功后的登录日志
     */
    SysLogininfor createSysLogininfor(SysLogininfor sysLogininfor);

    /**
     * 按 ID 查询登录日志
     *
     * @param id 登录日志 ID
     * @return 登录日志
     */
    SysLogininfor getSysLogininfor(Long id);

    /**
     * 分页查询登录日志
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysLogininfor> pageSysLogininfors(SysLogininforQueryParam query);

    /**
     * 列表查询登录日志
     *
     * @param query 查询参数
     * @return 登录日志列表
     */
    List<SysLogininfor> listSysLogininfors(SysLogininforQueryParam query);

    /**
     * 更新登录日志（全量）。
     *
     * @param sysLogininfor 登录日志（含主键）
     */
    void updateSysLogininfor(SysLogininfor sysLogininfor);

    /**
     * 按条件更新登录日志（只更新传入的非空字段）。
     *
     * @param sysLogininfor 登录日志（至少含主键）
     */
    void updateByCondition(SysLogininfor sysLogininfor);

    /**
     * 删除登录日志。
     *
     * @param id 登录日志 ID
     */
    void deleteSysLogininfor(Long id);
}
