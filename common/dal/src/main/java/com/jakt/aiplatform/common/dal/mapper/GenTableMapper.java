package com.jakt.aiplatform.common.dal.mapper;

import com.jakt.aiplatform.common.dal.dataobject.GenTableDO;
import com.jakt.aiplatform.core.model.param.GenTableQueryParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 代码生成 Mapper。SQL 全部在 resources/mapper/GenTableMapper.xml 中。
 */
@Mapper
public interface GenTableMapper {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 代码生成数据对象
     */
    GenTableDO selectById(Long id);

    /**
     * 分页查询：SQL 含 LIMIT #{offset}, #{pageSize}，配合 countByQuery 组装分页结果。
     *
     * @param query 查询参数
     * @return 当前页数据
     */
    List<GenTableDO> selectPage(GenTableQueryParam query);

    /**
     * 列表查询：与 {@link #selectPage} 完全一致，仅去掉 LIMIT 一行，返回全量结果。
     *
     * @param query 查询参数
     * @return 全量数据
     */
    List<GenTableDO> selectList(GenTableQueryParam query);

    /**
     * 按查询条件统计总条数，用于分页。
     *
     * @param query 查询参数
     * @return 总条数
     */
    long countByQuery(GenTableQueryParam query);

    /**
     * 新增，返回受影响行数；自增主键回填到 {@code genTableDO.id}。
     *
     * @param genTableDO 数据对象
     * @return 受影响行数
     */
    int insert(GenTableDO genTableDO);

    /**
     * 按主键更新，返回受影响行数。
     *
     * @param genTableDO 数据对象
     * @return 受影响行数
     */
    int update(GenTableDO genTableDO);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新），适合只改几个字段的场景。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；create_time/update_time 由数据库自动维护，不参与更新。
     *
     * @param genTableDO 数据对象（至少含主键）
     * @return 受影响行数
     */
    int updateByCondition(GenTableDO genTableDO);

    /**
     * 按主键删除，返回受影响行数。
     *
     * @param id 主键
     * @return 受影响行数
     */
    int deleteById(Long id);

    /**
     * 按条件查询代码生成业务表列表（table_name/table_comment 模糊）。
     *
     * @param query 查询参数
     * @return 代码生成业务表数据对象列表
     */
    List<GenTableDO> selectGenTableList(GenTableQueryParam query);

    /**
     * 查询数据库未导入的业务表（information_schema，排除 qrtz_/gen_ 与已导入表）。
     *
     * @param query 查询参数
     * @return 数据库业务表数据对象列表
     */
    List<GenTableDO> selectDbTableList(GenTableQueryParam query);

    /**
     * 按表名集合查询数据库业务表。
     *
     * @param tableNames 表名数组
     * @return 数据库业务表数据对象列表
     */
    List<GenTableDO> selectDbTableListByNames(String[] tableNames);

    /**
     * 查询全部代码生成业务表。
     *
     * @return 代码生成业务表数据对象列表
     */
    List<GenTableDO> selectGenTableAll();

    /**
     * 按表名查询代码生成业务表。
     *
     * @param tableName 表名
     * @return 代码生成业务表数据对象
     */
    GenTableDO selectGenTableByName(String tableName);

    /**
     * 按 ID 集合批量删除代码生成业务表。
     *
     * @param ids 主键数组
     * @return 影响行数
     */
    int deleteGenTableByIds(Long[] ids);

    /**
     * 执行建表 SQL（代码生成动态建表）。
     *
     * @param sql 建表 SQL
     * @return 影响行数
     */
    int createTable(String sql);
}
