package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.SysDictDataDO;
import com.jakt.aiplatform.core.model.domain.SysDictData;
import com.jakt.aiplatform.core.model.enums.IsDefaultEnum;
import com.jakt.aiplatform.core.model.enums.DictDataStatusEnum;
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
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
