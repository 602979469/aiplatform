package com.jakt.aiplatform.common.dal.mapper;

import com.jakt.aiplatform.common.dal.dataobject.ClusterImageDO;
import com.jakt.aiplatform.common.dal.query.ClusterImageDalQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 镜像表 Mapper。
 */
@Mapper
public interface ClusterImageMapper {
    /** 按主键查询。 */
    ClusterImageDO selectById(Long id);

    /** 分页查询。 */
    List<ClusterImageDO> selectPage(ClusterImageDalQuery query);

    /** 列表查询。 */
    List<ClusterImageDO> selectList(ClusterImageDalQuery query);

    /** 统计。 */
    long countByQuery(ClusterImageDalQuery query);

    /** 新增。 */
    int insert(ClusterImageDO imageDO);

    /** 更新（全量）。 */
    int update(ClusterImageDO imageDO);

    /** 条件更新。 */
    int updateByCondition(ClusterImageDO imageDO);

    /** 物理删除。 */
    int deleteById(Long id);
}
