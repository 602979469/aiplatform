package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysDictType;

import java.util.List;

/**
 * 字典类型仓储（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
public interface SysDictTypeRepository {

    /**
     * 查询字典类型列表。
     *
     * @param dictType 查询条件
     * @return 字典类型列表
     */
    List<SysDictType> selectDictTypeList(SysDictType dictType);

    /**
     * 查询全部字典类型。
     *
     * @return 字典类型列表
     */
    List<SysDictType> selectDictTypeAll();

    /**
     * 按主键查询字典类型。
     *
     * @param dictId 字典类型ID
     * @return 字典类型领域模型
     */
    SysDictType selectDictTypeById(Long dictId);

    /**
     * 按字典类型查询。
     *
     * @param dictType 字典类型
     * @return 字典类型领域模型
     */
    SysDictType selectDictTypeByType(String dictType);

    /**
     * 校验字典类型唯一。
     *
     * @param dictType 字典类型（含 dictId 用于排除自身）
     * @return 是否唯一
     */
    boolean checkDictTypeUnique(SysDictType dictType);

    /**
     * 按主键删除。
     *
     * @param dictId 字典类型ID
     * @return 影响行数
     */
    int deleteDictTypeById(Long dictId);

    /**
     * 按 ID 集合批量删除。
     *
     * @param ids 字典类型ID集合（逗号分隔）
     * @return 影响行数
     */
    int deleteDictTypeByIds(String ids);

    /**
     * 全量更新。
     *
     * @param dictType 字典类型
     * @return 影响行数
     */
    int updateDictType(SysDictType dictType);

    /**
     * 新增。
     *
     * @param dictType 字典类型
     * @return 影响行数
     */
    int insertDictType(SysDictType dictType);
}
