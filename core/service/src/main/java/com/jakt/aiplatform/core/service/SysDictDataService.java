package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.domain.SysDictData;
import com.jakt.aiplatform.core.model.param.SysDictDataQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 字典数据领域服务
 *
 * 实现类为 SysDictDataServiceImpl（core.service.impl 包）。
 */
public interface SysDictDataService {

    /**
     * 创建字典数据
     *
     * @param sysDictData 字典数据
     * @return 创建后的字典数据（主键已回填）
     */
    SysDictData createSysDictData(SysDictData sysDictData);

    /**
     * 更新字典数据（全量）
     *
     * @param sysDictData 字典数据（含主键）
     */
    void updateSysDictData(SysDictData sysDictData);

    /**
     * 按条件更新字典数据（只更新传入的非空字段）。
     *
     * @param sysDictData 字典数据（至少含主键）
     */
    void updateByCondition(SysDictData sysDictData);

    /**
     * 删除字典数据
     *
     * @param id 字典数据 ID
     */
    void deleteSysDictData(Long id);

    /**
     * 按 ID 获取字典数据
     *
     * @param id 字典数据 ID
     * @return 字典数据
     */
    SysDictData getSysDictData(Long id);

    /**
     * 分页查询字典数据
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysDictData> findPage(SysDictDataQueryParam query);

    /**
     * 列表查询字典数据
     *
     * @param query 查询参数
     * @return 字典数据列表
     */
    List<SysDictData> findList(SysDictDataQueryParam query);
}
