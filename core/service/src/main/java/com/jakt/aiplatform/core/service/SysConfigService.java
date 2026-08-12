package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.domain.SysConfig;
import com.jakt.aiplatform.core.model.param.SysConfigQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 参数配置领域服务
 *
 * 实现类为 SysConfigServiceImpl（core.service.impl 包）。
 */
public interface SysConfigService {

    /**
     * 创建参数配置
     *
     * @param sysConfig 参数配置
     * @return 创建后的参数配置（主键已回填）
     */
    SysConfig createSysConfig(SysConfig sysConfig);

    /**
     * 更新参数配置（全量）
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
     * 删除参数配置
     *
     * @param id 参数配置 ID
     */
    void deleteSysConfig(Long id);

    /**
     * 按 ID 获取参数配置
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
    PageResult<SysConfig> findPage(SysConfigQueryParam query);

    /**
     * 列表查询参数配置
     *
     * @param query 查询参数
     * @return 参数配置列表
     */
    List<SysConfig> findList(SysConfigQueryParam query);
}
