package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.core.model.domain.SysDictType;
import com.jakt.aiplatform.core.model.param.SysDictTypeQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 字典类型管理类接口定义
 * 
 */
public interface SysDictTypeManager {

    /**
     * 创建字典类型
     *
     * @param sysDictType 字典类型
     * @return 创建成功后的字典类型
     */
    SysDictType createSysDictType(SysDictType sysDictType);

    /**
     * 按 ID 查询字典类型
     *
     * @param id 字典类型 ID
     * @return 字典类型
     */
    SysDictType getSysDictType(Long id);

    /**
     * 分页查询字典类型
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysDictType> pageSysDictTypes(SysDictTypeQueryParam query);

    /**
     * 列表查询字典类型
     *
     * @param query 查询参数
     * @return 字典类型列表
     */
    List<SysDictType> listSysDictTypes(SysDictTypeQueryParam query);

    /**
     * 更新字典类型（全量）。
     *
     * @param sysDictType 字典类型（含主键）
     */
    void updateSysDictType(SysDictType sysDictType);

    /**
     * 按条件更新字典类型（只更新传入的非空字段）。
     *
     * @param sysDictType 字典类型（至少含主键）
     */
    void updateByCondition(SysDictType sysDictType);

    /**
     * 删除字典类型。
     *
     * @param id 字典类型 ID
     */
    void deleteSysDictType(Long id);
}
