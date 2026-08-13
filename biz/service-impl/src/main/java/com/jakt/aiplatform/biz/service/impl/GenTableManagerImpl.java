package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.GenTableManager;
import com.jakt.aiplatform.core.service.GenTableService;
import org.springframework.stereotype.Service;

/**
 * GenTable 管理实现类（RuoYi 移植过渡：方法清空，构造器保留待用例编排嵌入）。
 */
@Service
public class GenTableManagerImpl implements GenTableManager {

    /** GenTable 领域服务。 */
    private final GenTableService genTableService;

    public GenTableManagerImpl(GenTableService genTableService) {
        this.genTableService = genTableService;
    }
}
