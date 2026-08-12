package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysDictTypeDO;
import com.jakt.aiplatform.core.model.domain.SysDictType;
import com.jakt.aiplatform.core.model.enums.DictTypeStatusEnum;
import cn.hutool.core.util.ObjectUtil;


/**
 * 字典类型 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class SysDictTypeConvertor {

    private SysDictTypeConvertor() {
    }

    /**
     * DO → 领域模型。
     *
     * @param sysDictTypeDO 字典类型数据对象；为空返回 null
     * @return 字典类型领域模型
     */
    public static SysDictType toModel(SysDictTypeDO source) {
        if (source == null) {
            return null;
        }
        SysDictType target = new SysDictType();
        target.setDictId(source.getDictId());
        target.setDictName(source.getDictName());
        target.setDictType(source.getDictType());
        target.setStatus(DictTypeStatusEnum.fromCode(source.getStatus()));
        target.setRemark(source.getRemark());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param sysDictType 字典类型领域模型
     * @return 字典类型数据对象
     */
    public static SysDictTypeDO toDO(SysDictType source) {
        SysDictTypeDO target = new SysDictTypeDO();
        target.setDictId(source.getDictId());
        target.setDictName(source.getDictName());
        target.setDictType(source.getDictType());
        target.setStatus(ObjectUtil.isNull(source.getStatus()) ? null : source.getStatus().getCode());
        target.setRemark(source.getRemark());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
