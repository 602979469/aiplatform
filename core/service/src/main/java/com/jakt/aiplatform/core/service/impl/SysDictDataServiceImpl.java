package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.model.domain.SysDictData;
import com.jakt.aiplatform.core.model.param.SysDictDataQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.repository.SysDictDataRepository;
import com.jakt.aiplatform.core.service.SysDictDataService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典数据领域服务实现：承载字典数据相关的业务规则。只写规则，不碰持久化细节。
 */
@Service
public class SysDictDataServiceImpl implements SysDictDataService {

    /** 字典数据仓储。 */
    private final SysDictDataRepository sysDictDataRepository;

    public SysDictDataServiceImpl(SysDictDataRepository sysDictDataRepository) {
        this.sysDictDataRepository = sysDictDataRepository;
    }

    @Override
    public SysDictData createSysDictData(SysDictData sysDictData) {
        return sysDictDataRepository.insert(sysDictData);
    }

    @Override
    public void updateSysDictData(SysDictData sysDictData) {
        sysDictDataRepository.update(sysDictData);
    }

    @Override
    public void updateByCondition(SysDictData sysDictData) {
        sysDictDataRepository.updateByCondition(sysDictData);
    }

    @Override
    public void deleteSysDictData(Long id) {
        sysDictDataRepository.deleteById(id);
    }

    @Override
    public SysDictData getSysDictData(Long id) {
        return sysDictDataRepository.findById(id);
    }

    @Override
    public PageResult<SysDictData> findPage(SysDictDataQueryParam query) {
        return sysDictDataRepository.findPage(query);
    }

    @Override
    public List<SysDictData> findList(SysDictDataQueryParam query) {
        return sysDictDataRepository.findList(query);
    }
}
