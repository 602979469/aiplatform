package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.ClusterPodConfig;
import com.jakt.aiplatform.core.model.param.ClusterPodConfigQueryParam;
import com.jakt.aiplatform.core.repository.ClusterPodConfigRepository;
import com.jakt.aiplatform.core.service.ClusterPodConfigService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 业务pod配置表领域服务实现：承载业务pod配置表相关的业务规则。只写规则，不碰持久化细节。
 */
@Service
public class ClusterPodConfigServiceImpl implements ClusterPodConfigService {

    /** 业务pod配置表仓储。 */
    private final ClusterPodConfigRepository clusterPodConfigRepository;

    public ClusterPodConfigServiceImpl(ClusterPodConfigRepository clusterPodConfigRepository) {
        this.clusterPodConfigRepository = clusterPodConfigRepository;
    }

    @Override
    public ClusterPodConfig createClusterPodConfig(ClusterPodConfig clusterPodConfig) {
        return clusterPodConfigRepository.insert(clusterPodConfig);
    }

    @Override
    public int updateClusterPodConfig(ClusterPodConfig clusterPodConfig) {
        return clusterPodConfigRepository.update(clusterPodConfig);
    }

    @Override
    public int updateByCondition(ClusterPodConfig clusterPodConfig) {
        return clusterPodConfigRepository.updateByCondition(clusterPodConfig);
    }

    @Override
    public int deleteClusterPodConfig(Long id) {
        return clusterPodConfigRepository.deleteById(id);
    }

    @Override
    public ClusterPodConfig getClusterPodConfig(Long id) {
        return clusterPodConfigRepository.findById(id);
    }

    @Override
    public PageResult<ClusterPodConfig> findPage(ClusterPodConfigQueryParam query) {
        return clusterPodConfigRepository.findPage(query);
    }

    @Override
    public List<ClusterPodConfig> findList(ClusterPodConfigQueryParam query) {
        return clusterPodConfigRepository.findList(query);
    }
}
