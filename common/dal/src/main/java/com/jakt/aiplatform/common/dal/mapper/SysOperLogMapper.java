package com.jakt.aiplatform.common.dal.mapper;

import com.jakt.aiplatform.common.dal.dataobject.SysOperLogDO;
import com.jakt.aiplatform.core.model.param.SysOperLogQueryParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 操作日志 Mapper。SQL 全部在 resources/mapper/SysOperLogMapper.xml 中。
 */
@Mapper
public interface SysOperLogMapper {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 操作日志数据对象
     */
    SysOperLogDO selectById(Long id);

    /**
     * 分页查询：SQL 含 LIMIT #{offset}, #{pageSize}，配合 countByQuery 组装分页结果。
     *
     * @param query 查询参数
     * @return 当前页数据
     */
    List<SysOperLogDO> selectPage(SysOperLogQueryParam query);

    /**
     * 列表查询：与 {@link #selectPage} 完全一致，仅去掉 LIMIT 一行，返回全量结果。
     *
     * @param query 查询参数
     * @return 全量数据
     */
    List<SysOperLogDO> selectList(SysOperLogQueryParam query);

    /**
     * 按查询条件统计总条数，用于分页。
     *
     * @param query 查询参数
     * @return 总条数
     */
    long countByQuery(SysOperLogQueryParam query);

    /**
     * 新增，返回受影响行数；自增主键回填到 {@code sysOperLogDO.id}。
     *
     * @param sysOperLogDO 数据对象
     * @return 受影响行数
     */
    int insert(SysOperLogDO sysOperLogDO);

    /**
     * 按主键更新，返回受影响行数。
     *
     * @param sysOperLogDO 数据对象
     * @return 受影响行数
     */
    int update(SysOperLogDO sysOperLogDO);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新），适合只改几个字段的场景。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；create_time/update_time 由数据库自动维护，不参与更新。
     *
     * @param sysOperLogDO 数据对象（至少含主键）
     * @return 受影响行数
     */
    int updateByCondition(SysOperLogDO sysOperLogDO);

    /**
     * 按主键删除，返回受影响行数。
     *
     * @param id 主键
     * @return 受影响行数
     */
    int deleteById(Long id);
}
