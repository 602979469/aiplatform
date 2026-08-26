package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.ClusterPodConfigDO;
import com.jakt.aiplatform.common.dal.mapper.ClusterPodConfigMapper;
import com.jakt.aiplatform.common.dal.query.ClusterPodConfigDalQuery;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.core.model.domain.ClusterPodConfig;
import com.jakt.aiplatform.core.model.param.ClusterPodConfigQueryParam;
import com.jakt.aiplatform.core.repository.ClusterPodConfigRepository;
import com.jakt.aiplatform.core.repository.convertor.ClusterPodConfigConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 业务pod配置表仓储：封装 Mapper，对外只暴露领域模型。单表操作不引入事务，多写事务由 core-service 编排。
 */
@Repository
public class ClusterPodConfigRepositoryImpl implements ClusterPodConfigRepository {

    /** 业务pod配置表 Mapper。 */
    private final ClusterPodConfigMapper clusterPodConfigMapper;

    public ClusterPodConfigRepositoryImpl(ClusterPodConfigMapper clusterPodConfigMapper) {
        this.clusterPodConfigMapper = clusterPodConfigMapper;
    }

    @Override
    public ClusterPodConfig findById(Long id) {
        ClusterPodConfigDO clusterPodConfigDO = clusterPodConfigMapper.selectById(id);
        return ClusterPodConfigConvertor.toModel(clusterPodConfigDO);
    }

    @Override
    public List<ClusterPodConfig> findList(ClusterPodConfigQueryParam query) {
        ClusterPodConfigDalQuery dalQuery = ClusterPodConfigConvertor.toDalQuery(query);
        List<ClusterPodConfigDO> doList = clusterPodConfigMapper.selectList(dalQuery);
        return ConvertUtil.map(doList, ClusterPodConfigConvertor::toModel);
    }

    @Override
    public ClusterPodConfig findOne(ClusterPodConfigQueryParam query) {
        ClusterPodConfigDalQuery dalQuery = ClusterPodConfigConvertor.toDalQuery(query);
        ClusterPodConfigDO row = clusterPodConfigMapper.selectOne(dalQuery);
        return row == null ? null : ClusterPodConfigConvertor.toModel(row);
    }

    @Override
    public PageResult<ClusterPodConfig> findPage(ClusterPodConfigQueryParam query) {
        ClusterPodConfigDalQuery dalQuery = ClusterPodConfigConvertor.toDalQuery(query);
        List<ClusterPodConfigDO> doList = clusterPodConfigMapper.selectPage(dalQuery);
        long total = clusterPodConfigMapper.countByQuery(dalQuery);
        List<ClusterPodConfig> list = ConvertUtil.map(doList, ClusterPodConfigConvertor::toModel);
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public ClusterPodConfig insert(ClusterPodConfig clusterPodConfig) {
        ClusterPodConfigDO clusterPodConfigDO = ClusterPodConfigConvertor.toDO(clusterPodConfig);
        clusterPodConfigMapper.insert(clusterPodConfigDO);
        // 主键回填到入参（自增主键由数据库生成），调用方直接使用原对象
        clusterPodConfig.setId(clusterPodConfigDO.getId());
        return clusterPodConfig;
    }

    @Override
    public int update(ClusterPodConfig clusterPodConfig) {
        ClusterPodConfigDO clusterPodConfigDO = ClusterPodConfigConvertor.toDO(clusterPodConfig);
        int affected = clusterPodConfigMapper.update(clusterPodConfigDO);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "ClusterPodConfigRepository.update id={} 影响行数={}",
                clusterPodConfig.getId(), affected);
        return affected;
    }

    @Override
    public int updateByCondition(ClusterPodConfig clusterPodConfig) {
        int affected = clusterPodConfigMapper.updateByCondition(ClusterPodConfigConvertor.toDO(clusterPodConfig));
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "ClusterPodConfigRepository.updateByCondition id={} 影响行数={}",
                clusterPodConfig.getId(), affected);
        return affected;
    }

    @Override
    public int deleteById(Long id) {
        int affected = clusterPodConfigMapper.deleteById(id);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "ClusterPodConfigRepository.deleteById id={} 影响行数={}",
                id, affected);
        return affected;
    }
}
