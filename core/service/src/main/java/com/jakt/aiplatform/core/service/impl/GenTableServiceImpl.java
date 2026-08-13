package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.repository.GenTableRepository;
import com.jakt.aiplatform.core.service.GenTableService;
import org.springframework.stereotype.Service;

/**
 * GenTable 领域服务实现（RuoYi 移植过渡：方法清空，构造器保留待嵌入 RuoYi service 逻辑）。
 */
@Service
public class GenTableServiceImpl implements GenTableService {

    /** GenTable 仓储。 */
    private final GenTableRepository genTableRepository;

    public GenTableServiceImpl(GenTableRepository genTableRepository) {
        this.genTableRepository = genTableRepository;
    }
}
