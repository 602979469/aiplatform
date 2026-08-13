package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysDictTypeDO;
import com.jakt.aiplatform.common.dal.mapper.SysDictTypeMapper;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.common.util.tools.ListUtil;
import com.jakt.aiplatform.core.model.domain.SysDictType;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.param.SysDictTypeQueryParam;
import com.jakt.aiplatform.core.repository.SysDictTypeRepository;
import com.jakt.aiplatform.core.repository.convertor.SysDictTypeConvertor;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 字典类型仓储实现（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
@Repository
public class SysDictTypeRepositoryImpl implements SysDictTypeRepository {

    /** 字典类型 Mapper。 */
    private final SysDictTypeMapper sysDictTypeMapper;

    public SysDictTypeRepositoryImpl(SysDictTypeMapper sysDictTypeMapper) {
        this.sysDictTypeMapper = sysDictTypeMapper;
    }

    private SysDictType findOne(SysDictTypeDO sysDictTypeDO) {
        SysDictTypeQueryParam query = SysDictTypeConvertor.toQueryParam(sysDictTypeDO);
        List<SysDictTypeDO> list = sysDictTypeMapper.selectList(query);
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        AiPlatformInvoker.throwErrWhenTrue(list.size() > 1, ErrorCodeEnum.RESULT_NOT_UNIQUE, "查询结果不唯一");
        return SysDictTypeConvertor.toModel(list.get(0));
    }

    @Override
    public List<SysDictType> selectDictTypeList(SysDictType dictType) {
        List<SysDictTypeDO> list = sysDictTypeMapper.selectList(SysDictTypeConvertor.toQueryParam(dictType));
        return ListUtil.convert(list, SysDictTypeConvertor::toModel);
    }

    @Override
    public List<SysDictType> selectDictTypeAll() {
        List<SysDictTypeDO> list = sysDictTypeMapper.selectList(new SysDictTypeQueryParam());
        return ListUtil.convert(list, SysDictTypeConvertor::toModel);
    }

    @Override
    public SysDictType selectDictTypeById(Long dictId) {
        return SysDictTypeConvertor.toModel(sysDictTypeMapper.selectById(dictId));
    }

    @Override
    public SysDictType selectDictTypeByType(String dictType) {
        SysDictTypeDO condition = new SysDictTypeDO();
        condition.setDictType(dictType);
        return findOne(condition);
    }

    @Override
    public boolean checkDictTypeUnique(SysDictType dictType) {
        SysDictTypeQueryParam query = new SysDictTypeQueryParam();
        query.setDictType(dictType.getDictType());
        List<SysDictTypeDO> list = sysDictTypeMapper.selectList(query);
        if (CollUtil.isEmpty(list)) {
            return true;
        }
        if (list.size() > 1) {
            return false;
        }
        return ObjectUtil.equal(list.get(0).getDictId(), dictType.getDictId());
    }

    @Override
    public int deleteDictTypeById(Long dictId) {
        return sysDictTypeMapper.deleteById(dictId);
    }

    @Override
    public int deleteDictTypeByIds(String ids) {
        return sysDictTypeMapper.deleteByIds(Convert.toLongArray(ids));
    }

    @Override
    public int updateDictType(SysDictType dictType) {
        return sysDictTypeMapper.update(SysDictTypeConvertor.toDO(dictType));
    }

    @Override
    public int insertDictType(SysDictType dictType) {
        return sysDictTypeMapper.insert(SysDictTypeConvertor.toDO(dictType));
    }
}
