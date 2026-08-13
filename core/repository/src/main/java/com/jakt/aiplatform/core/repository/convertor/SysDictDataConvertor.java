package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysDictDataDO;
import com.jakt.aiplatform.core.model.domain.SysDictData;
import com.jakt.aiplatform.core.model.enums.IsDefaultEnum;
import com.jakt.aiplatform.core.model.enums.DictDataStatusEnum;
import com.jakt.aiplatform.core.model.param.SysDictDataQueryParam;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;


/**
 * 字典数据 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class SysDictDataConvertor {

    private SysDictDataConvertor() {
    }

    /**
     * 领域模型 → 查询参数（枚举转 code，显式赋值）。
     *
     * @param dictData 字典数据领域模型
     * @return 字典数据查询参数
     */
    public static SysDictDataQueryParam toQueryParam(SysDictData dictData) {
        SysDictDataQueryParam query = new SysDictDataQueryParam();
        query.setDictCode(dictData.getDictCode());
        query.setDictSort(dictData.getDictSort() == null ? null : dictData.getDictSort().intValue());
        query.setDictLabel(dictData.getDictLabel());
        query.setDictValue(dictData.getDictValue());
        query.setDictType(dictData.getDictType());
        query.setCssClass(dictData.getCssClass());
        query.setListClass(dictData.getListClass());
        query.setIsDefault(dictData.getIsDefault() == null ? null : dictData.getIsDefault().getCode());
        query.setStatus(dictData.getStatus() == null ? null : dictData.getStatus().getCode());
        query.setRemark(dictData.getRemark());
        return query;
    }

    /**
     * 数据对象 → 查询参数（显式赋值，仅拷贝查询相关字段）。
     *
     * @param condition 字典数据对象（条件载体）
     * @return 字典数据查询参数
     */
    public static SysDictDataQueryParam toQueryParam(SysDictDataDO condition) {
        SysDictDataQueryParam query = new SysDictDataQueryParam();
        query.setDictCode(condition.getDictCode());
        query.setDictSort(condition.getDictSort());
        query.setDictLabel(condition.getDictLabel());
        query.setDictValue(condition.getDictValue());
        query.setDictType(condition.getDictType());
        query.setCssClass(condition.getCssClass());
        query.setListClass(condition.getListClass());
        query.setIsDefault(condition.getIsDefault());
        query.setStatus(condition.getStatus());
        query.setRemark(condition.getRemark());
        return query;
    }

    /**
     * DO → 领域模型。
     *
     * @param sysDictDataDO 字典数据数据对象；为空返回 null
     * @return 字典数据领域模型
     */
    public static SysDictData toModel(SysDictDataDO source) {
        if (source == null) {
            return null;
        }
        SysDictData target = new SysDictData();
        target.setDictCode(source.getDictCode());
        target.setDictSort(Convert.toLong(source.getDictSort()));
        target.setDictLabel(source.getDictLabel());
        target.setDictValue(source.getDictValue());
        target.setDictType(source.getDictType());
        target.setCssClass(source.getCssClass());
        target.setListClass(source.getListClass());
        target.setIsDefault(IsDefaultEnum.fromCode(source.getIsDefault()));
        target.setStatus(DictDataStatusEnum.fromCode(source.getStatus()));
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
     * @param sysDictData 字典数据领域模型
     * @return 字典数据数据对象
     */
    public static SysDictDataDO toDO(SysDictData source) {
        SysDictDataDO target = new SysDictDataDO();
        target.setDictCode(source.getDictCode());
        target.setDictSort(Convert.toInt(source.getDictSort()));
        target.setDictLabel(source.getDictLabel());
        target.setDictValue(source.getDictValue());
        target.setDictType(source.getDictType());
        target.setCssClass(source.getCssClass());
        target.setListClass(source.getListClass());
        target.setIsDefault(ObjectUtil.isNull(source.getIsDefault()) ? null : source.getIsDefault().getCode());
        target.setStatus(ObjectUtil.isNull(source.getStatus()) ? null : source.getStatus().getCode());
        target.setRemark(source.getRemark());
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
