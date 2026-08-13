package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.GenTableColumnManager;
import com.jakt.aiplatform.core.service.GenTableColumnService;
import org.springframework.stereotype.Service;

/**
 * GenTableColumn 管理实现类（RuoYi 移植过渡：方法清空，构造器保留待用例编排嵌入）。
 */
@Service
public class GenTableColumnManagerImpl implements GenTableColumnManager {

    /** GenTableColumn 领域服务。 */
    private final GenTableColumnService genTableColumnService;

    public GenTableColumnManagerImpl(GenTableColumnService genTableColumnService) {
        this.genTableColumnService = genTableColumnService;
    }
}
