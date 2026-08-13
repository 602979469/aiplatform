package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysDictDataDO;
import com.jakt.aiplatform.common.dal.mapper.SysDictDataMapper;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.common.util.tools.ListUtil;
import com.jakt.aiplatform.core.model.domain.SysDictData;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.param.SysDictDataQueryParam;
import com.jakt.aiplatform.core.repository.SysDictDataRepository;
import com.jakt.aiplatform.core.repository.convertor.SysDictDataConvertor;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 字典数据仓储实现（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
@Repository
public class SysDictDataRepositoryImpl implements SysDictDataRepository {

    /** 字典数据 Mapper。 */
    private final SysDictDataMapper sysDictDataMapper;

    public SysDictDataRepositoryImpl(SysDictDataMapper sysDictDataMapper) {
        this.sysDictDataMapper = sysDictDataMapper;
    }

    private SysDictData findOne(SysDictDataDO sysDictDataDO) {
        SysDictDataQueryParam query = SysDictDataConvertor.toQueryParam(sysDictDataDO);
        List<SysDictDataDO> list = sysDictDataMapper.selectList(query);
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        AiPlatformInvoker.throwErrWhenTrue(list.size() > 1, ErrorCodeEnum.RESULT_NOT_UNIQUE, "查询结果不唯一");
        return SysDictDataConvertor.toModel(list.get(0));
    }

    @Override
    public List<SysDictData> selectDictDataList(SysDictData dictData) {
        List<SysDictDataDO> list = sysDictDataMapper.selectList(SysDictDataConvertor.toQueryParam(dictData));
        return ListUtil.convert(list, SysDictDataConvertor::toModel);
    }

    @Override
    public List<SysDictData> selectDictDataByType(String dictType) {
        SysDictDataQueryParam query = new SysDictDataQueryParam();
        query.setDictType(dictType);
        List<SysDictDataDO> list = sysDictDataMapper.selectList(query);
        return ListUtil.convert(list, SysDictDataConvertor::toModel);
    }

    @Override
    public String selectDictLabel(SysDictData dictData) {
        SysDictDataDO condition = new SysDictDataDO();
        condition.setDictType(dictData.getDictType());
        condition.setDictValue(dictData.getDictValue());
        SysDictData data = findOne(condition);
        return data == null ? null : data.getDictLabel();
    }

    @Override
    public SysDictData selectDictDataById(Long dictCode) {
        return SysDictDataConvertor.toModel(sysDictDataMapper.selectById(dictCode));
    }

    @Override
    public int countDictDataByType(SysDictData dictData) {
        SysDictDataQueryParam query = new SysDictDataQueryParam();
        query.setDictType(dictData.getDictType());
        return (int) sysDictDataMapper.countByQuery(query);
    }

    @Override
    public int deleteDictDataById(Long dictCode) {
        return sysDictDataMapper.deleteById(dictCode);
    }

    @Override
    public int deleteDictDataByIds(String ids) {
        return sysDictDataMapper.deleteByIds(Convert.toLongArray(ids));
    }

    @Override
    public int updateDictData(SysDictData dictData) {
        return sysDictDataMapper.update(SysDictDataConvertor.toDO(dictData));
    }

    @Override
    public int updateDictDataType(String oldDictType, String newDictType) {
        return sysDictDataMapper.updateDictDataType(oldDictType, newDictType);
    }

    @Override
    public int insertDictData(SysDictData dictData) {
        return sysDictDataMapper.insert(SysDictDataConvertor.toDO(dictData));
    }
}
