package com.jakt.aiplatform.common.dal.mapper;

import com.jakt.aiplatform.common.dal.dataobject.SysDictDataDO;
import com.jakt.aiplatform.core.model.param.SysDictDataQueryParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 字典数据 Mapper。SQL 全部在 resources/mapper/SysDictDataMapper.xml 中。
 */
@Mapper
public interface SysDictDataMapper {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 字典数据数据对象
     */
    SysDictDataDO selectById(Long id);

    /**
     * 分页查询：SQL 含 LIMIT #{offset}, #{pageSize}，配合 countByQuery 组装分页结果。
     *
     * @param query 查询参数
     * @return 当前页数据
     */
    List<SysDictDataDO> selectPage(SysDictDataQueryParam query);

    /**
     * 列表查询：与 {@link #selectPage} 完全一致，仅去掉 LIMIT 一行，返回全量结果。
     *
     * @param query 查询参数
     * @return 全量数据
     */
    List<SysDictDataDO> selectList(SysDictDataQueryParam query);

    /**
     * 按查询条件统计总条数，用于分页。
     *
     * @param query 查询参数
     * @return 总条数
     */
    long countByQuery(SysDictDataQueryParam query);

    /**
     * 新增，返回受影响行数；自增主键回填到 {@code sysDictDataDO.id}。
     *
     * @param sysDictDataDO 数据对象
     * @return 受影响行数
     */
    int insert(SysDictDataDO sysDictDataDO);

    /**
     * 按主键更新，返回受影响行数。
     *
     * @param sysDictDataDO 数据对象
     * @return 受影响行数
     */
    int update(SysDictDataDO sysDictDataDO);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新），适合只改几个字段的场景。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；create_time/update_time 由数据库自动维护，不参与更新。
     *
     * @param sysDictDataDO 数据对象（至少含主键）
     * @return 受影响行数
     */
    int updateByCondition(SysDictDataDO sysDictDataDO);

    /**
     * 按主键删除，返回受影响行数。
     *
     * @param id 主键
     * @return 受影响行数
     */
    int deleteById(Long id);

    /**
     * 按 ID 集合批量删除。
     *
     * @param ids 主键数组
     * @return 影响行数
     */
    int deleteByIds(Long[] ids);

    /**
     * 按字典类型批量修改字典类型。
     *
     * @param oldDictType 原字典类型
     * @param newDictType 新字典类型
     * @return 影响行数
     */
    int updateDictDataType(String oldDictType, String newDictType);
}
