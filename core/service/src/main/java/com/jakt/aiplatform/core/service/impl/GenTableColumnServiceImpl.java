package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.model.domain.GenTableColumn;
import com.jakt.aiplatform.core.model.param.GenTableColumnQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.repository.GenTableColumnRepository;
import com.jakt.aiplatform.core.service.GenTableColumnService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 代码生成字段领域服务实现：承载代码生成字段相关的业务规则。只写规则，不碰持久化细节。
 */
@Service
public class GenTableColumnServiceImpl implements GenTableColumnService {

    /** 代码生成字段仓储。 */
    private final GenTableColumnRepository genTableColumnRepository;

    public GenTableColumnServiceImpl(GenTableColumnRepository genTableColumnRepository) {
        this.genTableColumnRepository = genTableColumnRepository;
    }

    @Override
    public GenTableColumn createGenTableColumn(GenTableColumn genTableColumn) {
        return genTableColumnRepository.insert(genTableColumn);
    }

    @Override
    public void updateGenTableColumn(GenTableColumn genTableColumn) {
        genTableColumnRepository.update(genTableColumn);
    }

    @Override
    public void updateByCondition(GenTableColumn genTableColumn) {
        genTableColumnRepository.updateByCondition(genTableColumn);
    }

    @Override
    public void deleteGenTableColumn(Long id) {
        genTableColumnRepository.deleteById(id);
    }

    @Override
    public GenTableColumn getGenTableColumn(Long id) {
        return genTableColumnRepository.findById(id);
    }

    @Override
    public PageResult<GenTableColumn> findPage(GenTableColumnQueryParam query) {
        return genTableColumnRepository.findPage(query);
    }

    @Override
    public List<GenTableColumn> findList(GenTableColumnQueryParam query) {
        return genTableColumnRepository.findList(query);
    }
}
