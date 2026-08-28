package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.common.framework.context.UserContext;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.core.model.domain.ClusterPodConfig;
import com.jakt.aiplatform.core.model.enums.BizErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.ClusterPodConfigStatusEnum;
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
        // 创建默认草稿
        clusterPodConfig.setStatus(ClusterPodConfigStatusEnum.DRAFT);
        clusterPodConfig.setCreateBy(UserContext.getUserId().toString());
        clusterPodConfig.setUpdateBy(UserContext.getUserId().toString());
        return clusterPodConfigRepository.insert(clusterPodConfig);
    }

    @Override
    public int updateClusterPodConfig(ClusterPodConfig clusterPodConfig) {
        ClusterPodConfig current = getClusterPodConfig(clusterPodConfig.getId());
        AssertUtil.throwErrWhenNull(current, BizErrorCodeEnum.RESOURCE_NOT_FOUND, "业务pod配置不存在");
        checkUpdateAllowed(current);
        // 更新不改变状态，保留当前状态
        clusterPodConfig.setStatus(current.getStatus());
        clusterPodConfig.setUpdateBy(UserContext.getUserId().toString());
        return clusterPodConfigRepository.update(clusterPodConfig);
    }

    @Override
    public int updateByCondition(ClusterPodConfig clusterPodConfig) {
        return clusterPodConfigRepository.updateByCondition(clusterPodConfig);
    }

    @Override
    public int deleteClusterPodConfig(List<Long> ids) {
        return clusterPodConfigRepository.deleteByIds(ids);
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

    @Override
    public void checkUpdateAllowed(ClusterPodConfig config) {
        ClusterPodConfigStatusEnum status = config.getStatus();
        AssertUtil.throwErrWhenTrue(
                status != ClusterPodConfigStatusEnum.DRAFT
                        && status != ClusterPodConfigStatusEnum.BUILD_FAILED,
                BizErrorCodeEnum.STATUS_NOT_ALLOWED,
                "当前状态(" + (status == null ? "未知" : status.getDesc()) + ")不允许修改");
    }

    @Override
    public void checkDeleteAllowed(ClusterPodConfig config) {
        ClusterPodConfigStatusEnum status = config.getStatus();
        AssertUtil.throwErrWhenTrue(
                status != ClusterPodConfigStatusEnum.DRAFT
                        && status != ClusterPodConfigStatusEnum.BUILD_FAILED,
                BizErrorCodeEnum.STATUS_NOT_ALLOWED,
                "当前状态(" + (status == null ? "未知" : status.getDesc()) + ")不允许删除");
    }

    @Override
    public void markBuilding(Long id) {
        ClusterPodConfig update = new ClusterPodConfig();
        update.setId(id);
        update.setStatus(ClusterPodConfigStatusEnum.BUILDING);
        clusterPodConfigRepository.updateByCondition(update);
    }

    @Override
    public void markBuildResult(Long id, boolean success) {
        ClusterPodConfig update = new ClusterPodConfig();
        update.setId(id);
        update.setStatus(success
                ? ClusterPodConfigStatusEnum.PUBLISHED
                : ClusterPodConfigStatusEnum.BUILD_FAILED);
        clusterPodConfigRepository.updateByCondition(update);
    }

    @Override
    public void retire(Long id) {
        ClusterPodConfig current = getClusterPodConfig(id);
        AssertUtil.throwErrWhenNull(current, BizErrorCodeEnum.RESOURCE_NOT_FOUND, "业务pod配置不存在");
        AssertUtil.throwErrWhenTrue(
                current.getStatus() != ClusterPodConfigStatusEnum.PUBLISHED,
                BizErrorCodeEnum.STATUS_NOT_ALLOWED,
                "仅发布状态可弃用");
        ClusterPodConfig update = new ClusterPodConfig();
        update.setId(id);
        update.setStatus(ClusterPodConfigStatusEnum.RETIRED);
        clusterPodConfigRepository.updateByCondition(update);
    }
}
