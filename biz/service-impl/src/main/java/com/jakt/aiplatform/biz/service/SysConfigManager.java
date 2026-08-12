package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.core.model.domain.SysConfig;
import com.jakt.aiplatform.core.model.param.SysConfigQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 参数配置管理类接口定义
 * 
 */
public interface SysConfigManager {

    /**
     * 创建参数配置
     *
     * @param sysConfig 参数配置
     * @return 创建成功后的参数配置
     */
    SysConfig createSysConfig(SysConfig sysConfig);

    /**
     * 按 ID 查询参数配置
     *
     * @param id 参数配置 ID
     * @return 参数配置
     */
    SysConfig getSysConfig(Long id);

    /**
     * 分页查询参数配置
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysConfig> pageSysConfigs(SysConfigQueryParam query);

    /**
     * 列表查询参数配置
     *
     * @param query 查询参数
     * @return 参数配置列表
     */
    List<SysConfig> listSysConfigs(SysConfigQueryParam query);

    /**
     * 更新参数配置（全量）。
     *
     * @param sysConfig 参数配置（含主键）
     */
    void updateSysConfig(SysConfig sysConfig);

    /**
     * 按条件更新参数配置（只更新传入的非空字段）。
     *
     * @param sysConfig 参数配置（至少含主键）
     */
    void updateByCondition(SysConfig sysConfig);

    /**
     * 删除参数配置。
     *
     * @param id 参数配置 ID
     */
    void deleteSysConfig(Long id);
}
