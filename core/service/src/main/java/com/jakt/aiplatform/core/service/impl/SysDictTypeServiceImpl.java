package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.model.domain.SysDictType;
import com.jakt.aiplatform.core.model.param.SysDictTypeQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.repository.SysDictTypeRepository;
import com.jakt.aiplatform.core.service.SysDictTypeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典类型领域服务实现：承载字典类型相关的业务规则。只写规则，不碰持久化细节。
 */
@Service
public class SysDictTypeServiceImpl implements SysDictTypeService {

    /** 字典类型仓储。 */
    private final SysDictTypeRepository sysDictTypeRepository;

    public SysDictTypeServiceImpl(SysDictTypeRepository sysDictTypeRepository) {
        this.sysDictTypeRepository = sysDictTypeRepository;
    }

    @Override
    public SysDictType createSysDictType(SysDictType sysDictType) {
        return sysDictTypeRepository.insert(sysDictType);
    }

    @Override
    public void updateSysDictType(SysDictType sysDictType) {
        sysDictTypeRepository.update(sysDictType);
    }

    @Override
    public void updateByCondition(SysDictType sysDictType) {
        sysDictTypeRepository.updateByCondition(sysDictType);
    }

    @Override
    public void deleteSysDictType(Long id) {
        sysDictTypeRepository.deleteById(id);
    }

    @Override
    public SysDictType getSysDictType(Long id) {
        return sysDictTypeRepository.findById(id);
    }

    @Override
    public PageResult<SysDictType> findPage(SysDictTypeQueryParam query) {
        return sysDictTypeRepository.findPage(query);
    }

    @Override
    public List<SysDictType> findList(SysDictTypeQueryParam query) {
        return sysDictTypeRepository.findList(query);
    }
}
