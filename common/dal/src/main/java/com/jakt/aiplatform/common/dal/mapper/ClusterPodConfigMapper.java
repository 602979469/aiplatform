package com.jakt.aiplatform.common.dal.mapper;

import com.jakt.aiplatform.common.dal.dataobject.ClusterPodConfigDO;
import com.jakt.aiplatform.common.dal.query.ClusterPodConfigDalQuery;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 业务pod配置表 Mapper。SQL 全部在 resources/mapper/ClusterPodConfigMapper.xml 中；
 * 查询参数使用 common-dal 的 ClusterPodConfigDalQuery，common-dal 不依赖 core-model。
 */
@Mapper
public interface ClusterPodConfigMapper {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 业务pod配置表数据对象
     */
    ClusterPodConfigDO selectById(Long id);

    /**
     * 分页查询：SQL 含 LIMIT #{offset}, #{pageSize}，配合 countByQuery 组装分页结果。
     *
     * @param query 查询参数
     * @return 当前页数据
     */
    List<ClusterPodConfigDO> selectPage(ClusterPodConfigDalQuery query);

    /**
     * 列表查询：与 {@link #selectPage} 完全一致，仅去掉 LIMIT 一行，返回全量结果。
     *
     * @param query 查询参数
     * @return 全量数据
     */
    List<ClusterPodConfigDO> selectList(ClusterPodConfigDalQuery query);

    /**
     * 单条查询：与 {@link #selectList} 一致但不加 LIMIT；多条由 MyBatis 抛 TooManyResultsException，不做特殊处理。
     *
     * @param query 查询参数
     * @return 至多一条数据，无匹配返回 null
     */
    ClusterPodConfigDO selectOne(ClusterPodConfigDalQuery query);

    /**
     * 按查询条件统计总条数，用于分页。
     *
     * @param query 查询参数
     * @return 总条数
     */
    long countByQuery(ClusterPodConfigDalQuery query);

    /**
     * 新增，返回受影响行数；自增主键回填到入参 DO。
     *
     * @param clusterPodConfigDO 数据对象
     * @return 受影响行数
     */
    int insert(ClusterPodConfigDO clusterPodConfigDO);

    /**
     * 按主键更新，返回受影响行数。
     *
     * @param clusterPodConfigDO 数据对象
     * @return 受影响行数
     */
    int update(ClusterPodConfigDO clusterPodConfigDO);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新），适合只改几个字段的场景。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；
     * update_time 由生成代码用 NOW() 维护。
     *
     * @param clusterPodConfigDO 数据对象（至少含主键）
     * @return 受影响行数
     */
    int updateByCondition(ClusterPodConfigDO clusterPodConfigDO);

    /**
     * 按主键删除，返回受影响行数。
     *
     * @param id 主键
     * @return 受影响行数
     */
    int deleteById(Long id);
}
