package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysConfigDO;
import com.jakt.aiplatform.core.model.domain.SysConfig;
import com.jakt.aiplatform.core.model.enums.ConfigTypeEnum;
import com.jakt.aiplatform.core.model.param.SysConfigQueryParam;
import cn.hutool.core.util.ObjectUtil;


/**
 * 参数配置 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class SysConfigConvertor {

    private SysConfigConvertor() {
    }

    /**
     * 领域模型 → 查询参数（枚举转 code，显式赋值）。
     *
     * @param config 参数配置领域模型
     * @return 参数配置查询参数
     */
    public static SysConfigQueryParam toQueryParam(SysConfig config) {
        SysConfigQueryParam query = new SysConfigQueryParam();
        query.setConfigId(config.getConfigId());
        query.setConfigName(config.getConfigName());
        query.setConfigKey(config.getConfigKey());
        query.setConfigValue(config.getConfigValue());
        query.setConfigType(config.getConfigType() == null ? null : config.getConfigType().getCode());
        query.setRemark(config.getRemark());
        return query;
    }

    /**
     * 数据对象 → 查询参数（显式赋值，仅拷贝查询相关字段）。
     *
     * @param condition 参数配置数据对象（条件载体）
     * @return 参数配置查询参数
     */
    public static SysConfigQueryParam toQueryParam(SysConfigDO condition) {
        SysConfigQueryParam query = new SysConfigQueryParam();
        query.setConfigId(condition.getConfigId());
        query.setConfigName(condition.getConfigName());
        query.setConfigKey(condition.getConfigKey());
        query.setConfigValue(condition.getConfigValue());
        query.setConfigType(condition.getConfigType());
        query.setRemark(condition.getRemark());
        return query;
    }

    /**
     * DO → 领域模型。
     *
     * @param sysConfigDO 参数配置数据对象；为空返回 null
     * @return 参数配置领域模型
     */
    public static SysConfig toModel(SysConfigDO source) {
        if (source == null) {
            return null;
        }
        SysConfig target = new SysConfig();
        target.setConfigId(source.getConfigId());
        target.setConfigName(source.getConfigName());
        target.setConfigKey(source.getConfigKey());
        target.setConfigValue(source.getConfigValue());
        target.setConfigType(ConfigTypeEnum.fromCode(source.getConfigType()));
        target.setRemark(source.getRemark());
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param sysConfig 参数配置领域模型
     * @return 参数配置数据对象
     */
    public static SysConfigDO toDO(SysConfig source) {
        SysConfigDO target = new SysConfigDO();
        target.setConfigId(source.getConfigId());
        target.setConfigName(source.getConfigName());
        target.setConfigKey(source.getConfigKey());
        target.setConfigValue(source.getConfigValue());
        target.setConfigType(ObjectUtil.isNull(source.getConfigType()) ? null : source.getConfigType().getCode());
        target.setRemark(source.getRemark());
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
