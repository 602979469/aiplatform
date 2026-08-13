package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysDictDataManager;
import com.jakt.aiplatform.core.service.SysDictDataService;
import org.springframework.stereotype.Service;

/**
 * 字典数据管理实现类（RuoYi 移植过渡：方法清空，构造器保留待用例编排嵌入）。
 */
@Service
public class SysDictDataManagerImpl implements SysDictDataManager {

    /** 字典数据领域服务。 */
    private final SysDictDataService sysDictDataService;

    public SysDictDataManagerImpl(SysDictDataService sysDictDataService) {
        this.sysDictDataService = sysDictDataService;
    }
}
