package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysDictData;

import java.util.List;

/**
 * 字典数据仓储（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
public interface SysDictDataRepository {

    /**
     * 查询字典数据列表。
     *
     * @param dictData 查询条件
     * @return 字典数据列表
     */
    List<SysDictData> selectDictDataList(SysDictData dictData);

    /**
     * 按字典类型查询字典数据列表。
     *
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    List<SysDictData> selectDictDataByType(String dictType);

    /**
     * 按字典类型 + 键值查询字典标签。
     *
     * @param dictData 字典数据（dictType/dictValue）
     * @return 字典标签
     */
    String selectDictLabel(SysDictData dictData);

    /**
     * 按主键查询字典数据。
     *
     * @param dictCode 字典编码
     * @return 字典数据领域模型
     */
    SysDictData selectDictDataById(Long dictCode);

    /**
     * 按字典类型统计数量。
     *
     * @param dictData 字典数据（dictType）
     * @return 数量
     */
    int countDictDataByType(SysDictData dictData);

    /**
     * 按主键删除。
     *
     * @param dictCode 字典编码
     * @return 影响行数
     */
    int deleteDictDataById(Long dictCode);

    /**
     * 按 ID 集合批量删除。
     *
     * @param ids 字典编码集合（逗号分隔）
     * @return 影响行数
     */
    int deleteDictDataByIds(String ids);

    /**
     * 全量更新。
     *
     * @param dictData 字典数据
     * @return 影响行数
     */
    int updateDictData(SysDictData dictData);

    /**
     * 按字典类型批量修改字典类型。
     *
     * @param oldDictType 原字典类型
     * @param newDictType 新字典类型
     * @return 影响行数
     */
    int updateDictDataType(String oldDictType, String newDictType);

    /**
     * 新增。
     *
     * @param dictData 字典数据
     * @return 影响行数
     */
    int insertDictData(SysDictData dictData);
}
