package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.repository.GenTableColumnRepository;
import com.jakt.aiplatform.core.service.GenTableColumnService;
import org.springframework.stereotype.Service;

/**
 * GenTableColumn 领域服务实现（RuoYi 移植过渡：方法清空，构造器保留待嵌入 RuoYi service 逻辑）。
 */
@Service
public class GenTableColumnServiceImpl implements GenTableColumnService {

    /** GenTableColumn 仓储。 */
    private final GenTableColumnRepository genTableColumnRepository;

    public GenTableColumnServiceImpl(GenTableColumnRepository genTableColumnRepository) {
        this.genTableColumnRepository = genTableColumnRepository;
    }
}
