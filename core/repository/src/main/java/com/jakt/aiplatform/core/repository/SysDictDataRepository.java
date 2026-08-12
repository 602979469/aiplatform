package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysDictData;
import com.jakt.aiplatform.core.model.param.SysDictDataQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 字典数据仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
public interface SysDictDataRepository {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 字典数据领域模型
     */
    SysDictData findById(Long id);

    /**
     * 分页查询。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysDictData> findPage(SysDictDataQueryParam query);

    /**
     * 列表查询。
     *
     * @param query 查询参数
     * @return 字典数据列表
     */
    List<SysDictData> findList(SysDictDataQueryParam query);

    /**
     * 新增。
     *
     * @param sysDictData 字典数据
     * @return 新增后的字典数据（主键已回填）
     */
    SysDictData insert(SysDictData sysDictData);

    /**
     * 更新。
     *
     * @param sysDictData 字典数据
     */
    void update(SysDictData sysDictData);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新）。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；create_time/update_time 由数据库自动维护。
     *
     * @param sysDictData 字典数据（至少含主键）
     */
    void updateByCondition(SysDictData sysDictData);

    /**
     * 按主键删除。
     *
     * @param id 主键
     */
    void deleteById(Long id);
}
