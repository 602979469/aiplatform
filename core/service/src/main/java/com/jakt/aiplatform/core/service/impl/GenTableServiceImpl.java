package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.model.domain.GenTable;
import com.jakt.aiplatform.core.model.param.GenTableQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.repository.GenTableRepository;
import com.jakt.aiplatform.core.service.GenTableService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 代码生成领域服务实现：承载代码生成相关的业务规则。只写规则，不碰持久化细节。
 */
@Service
public class GenTableServiceImpl implements GenTableService {

    /** 代码生成仓储。 */
    private final GenTableRepository genTableRepository;

    public GenTableServiceImpl(GenTableRepository genTableRepository) {
        this.genTableRepository = genTableRepository;
    }

    @Override
    public GenTable createGenTable(GenTable genTable) {
        return genTableRepository.insert(genTable);
    }

    @Override
    public void updateGenTable(GenTable genTable) {
        genTableRepository.update(genTable);
    }

    @Override
    public void updateByCondition(GenTable genTable) {
        genTableRepository.updateByCondition(genTable);
    }

    @Override
    public void deleteGenTable(Long id) {
        genTableRepository.deleteById(id);
    }

    @Override
    public GenTable getGenTable(Long id) {
        return genTableRepository.findById(id);
    }

    @Override
    public PageResult<GenTable> findPage(GenTableQueryParam query) {
        return genTableRepository.findPage(query);
    }

    @Override
    public List<GenTable> findList(GenTableQueryParam query) {
        return genTableRepository.findList(query);
    }
}
