package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysDictTypeDO;
import com.jakt.aiplatform.core.model.domain.SysDictType;
import com.jakt.aiplatform.core.model.enums.DictTypeStatusEnum;
import com.jakt.aiplatform.core.model.param.SysDictTypeQueryParam;
import cn.hutool.core.util.ObjectUtil;


/**
 * 字典类型 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class SysDictTypeConvertor {

    private SysDictTypeConvertor() {
    }

    /**
     * 领域模型 → 查询参数（枚举转 code，显式赋值）。
     *
     * @param dictType 字典类型领域模型
     * @return 字典类型查询参数
     */
    public static SysDictTypeQueryParam toQueryParam(SysDictType dictType) {
        SysDictTypeQueryParam query = new SysDictTypeQueryParam();
        query.setDictId(dictType.getDictId());
        query.setDictName(dictType.getDictName());
        query.setDictType(dictType.getDictType());
        query.setStatus(dictType.getStatus() == null ? null : dictType.getStatus().getCode());
        query.setRemark(dictType.getRemark());
        return query;
    }

    /**
     * 数据对象 → 查询参数（显式赋值，仅拷贝查询相关字段）。
     *
     * @param condition 字典类型数据对象（条件载体）
     * @return 字典类型查询参数
     */
    public static SysDictTypeQueryParam toQueryParam(SysDictTypeDO condition) {
        SysDictTypeQueryParam query = new SysDictTypeQueryParam();
        query.setDictId(condition.getDictId());
        query.setDictName(condition.getDictName());
        query.setDictType(condition.getDictType());
        query.setStatus(condition.getStatus());
        query.setRemark(condition.getRemark());
        return query;
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
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
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
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
