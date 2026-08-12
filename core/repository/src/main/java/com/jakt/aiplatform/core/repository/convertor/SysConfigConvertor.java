package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysConfigDO;
import com.jakt.aiplatform.core.model.domain.SysConfig;
import com.jakt.aiplatform.core.model.enums.ConfigTypeEnum;
import cn.hutool.core.util.ObjectUtil;


/**
 * 参数配置 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class SysConfigConvertor {

    private SysConfigConvertor() {
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
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
