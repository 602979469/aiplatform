package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysConfig;

import java.util.List;

/**
 * 参数配置仓储（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
public interface SysConfigRepository {

    /**
     * 按条件查询参数配置。
     *
     * @param config 查询条件（实体即条件）
     * @return 参数配置领域模型
     */
    SysConfig selectConfig(SysConfig config);

    /**
     * 查询参数配置列表。
     *
     * @param config 查询条件
     * @return 参数配置列表
     */
    List<SysConfig> selectConfigList(SysConfig config);

    /**
     * 按主键查询参数配置。
     *
     * @param configId 参数配置ID
     * @return 参数配置领域模型
     */
    SysConfig selectConfigById(Long configId);

    /**
     * 校验参数键名唯一。
     *
     * @param config 参数配置（含 configId 用于排除自身）
     * @return 是否唯一
     */
    boolean checkConfigKeyUnique(SysConfig config);

    /**
     * 新增参数配置。
     *
     * @param config 参数配置
     * @return 影响行数
     */
    int insertConfig(SysConfig config);

    /**
     * 全量更新参数配置。
     *
     * @param config 参数配置
     * @return 影响行数
     */
    int updateConfig(SysConfig config);

    /**
     * 按主键删除参数配置。
     *
     * @param configId 参数配置ID
     * @return 影响行数
     */
    int deleteConfigById(Long configId);

    /**
     * 按 ID 集合批量删除参数配置。
     *
     * @param ids 参数配置ID集合（逗号分隔）
     * @return 影响行数
     */
    int deleteConfigByIds(String ids);
}
