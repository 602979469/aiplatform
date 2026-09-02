package com.jakt.aiplatform.core.repository.convertor;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.dal.dataobject.ClusterPodConfigDO;
import com.jakt.aiplatform.common.dal.query.ClusterPodConfigDalQuery;
import com.jakt.aiplatform.common.framework.enums.BaseEnum;
import com.jakt.aiplatform.core.model.enums.ClusterPodConfigStatusEnum;
import com.jakt.aiplatform.core.model.domain.ClusterPodConfig;
import com.jakt.aiplatform.core.model.param.ClusterPodConfigQueryParam;


/**
 * 业务pod配置表 DO/领域模型/查询参数互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）；
 * QueryParam（core-model）→ DalQuery（common-dal）在 Repository 调 Mapper 前完成，common-dal 不依赖 core-model。
 */
public final class ClusterPodConfigConvertor {

    private ClusterPodConfigConvertor() {
    }

    /**
     * DO → 领域模型。
     *
     * @param clusterPodConfigDO 业务pod配置表数据对象；为空返回 null
     * @return 业务pod配置表领域模型
     */
    public static ClusterPodConfig toModel(ClusterPodConfigDO source) {
        if (source == null) {
            return null;
        }
        ClusterPodConfig target = new ClusterPodConfig();
        target.setId(source.getId());
        target.setResourceName(source.getResourceName());
        target.setPodName(source.getPodName());
        target.setNamespace(source.getNamespace());
        target.setDeployYaml(source.getDeployYaml());
        target.setImageId(source.getImageId());
        target.setAutoRefresh(source.getAutoRefresh());
        target.setLastBuiltCommit(source.getLastBuiltCommit());
        target.setStatus(BaseEnum.fromCode(ClusterPodConfigStatusEnum.class, source.getStatus()));
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
        target.setRemark(source.getRemark());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param clusterPodConfig 业务pod配置表领域模型
     * @return 业务pod配置表数据对象
     */
    public static ClusterPodConfigDO toDO(ClusterPodConfig source) {
        ClusterPodConfigDO target = new ClusterPodConfigDO();
        target.setId(source.getId());
        target.setResourceName(source.getResourceName());
        target.setPodName(source.getPodName());
        target.setNamespace(source.getNamespace());
        target.setDeployYaml(source.getDeployYaml());
        target.setImageId(source.getImageId());
        target.setAutoRefresh(source.getAutoRefresh());
        target.setLastBuiltCommit(source.getLastBuiltCommit());
        target.setStatus(ObjectUtil.isNull(source.getStatus()) ? null : source.getStatus().getCode());
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
        target.setRemark(source.getRemark());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 查询参数 → common-dal 查询参数。
     *
     * @param source 业务pod配置表查询参数；为空返回空对象
     * @return 业务pod配置表查询参数（common-dal）
     */
    public static ClusterPodConfigDalQuery toDalQuery(ClusterPodConfigQueryParam source) {
        ClusterPodConfigDalQuery target = new ClusterPodConfigDalQuery();
        if (source == null) {
            return target;
        }
        target.setPageNum(source.getPageNum());
        target.setPageSize(source.getPageSize());
        target.setId(source.getId());
        target.setResourceName(source.getResourceName());
        target.setPodName(source.getPodName());
        target.setNamespace(source.getNamespace());
        target.setDeployYaml(source.getDeployYaml());
        target.setAutoRefresh(source.getAutoRefresh());
        target.setLastBuiltCommit(source.getLastBuiltCommit());
        target.setStatus(ObjectUtil.isNull(source.getStatus()) ? null : source.getStatus().getCode());
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
        target.setRemark(source.getRemark());
        target.setCreateTimeBegin(source.getCreateTimeBegin());
        target.setCreateTimeEnd(source.getCreateTimeEnd());
        target.setUpdateTimeBegin(source.getUpdateTimeBegin());
        target.setUpdateTimeEnd(source.getUpdateTimeEnd());
        return target;
    }
}
