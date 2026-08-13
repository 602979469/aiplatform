package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.repository.SysDictDataRepository;
import com.jakt.aiplatform.core.service.SysDictDataService;
import org.springframework.stereotype.Service;

/**
 * 字典数据领域服务实现（RuoYi 移植过渡：方法清空，构造器保留待嵌入 RuoYi service 逻辑）。
 */
@Service
public class SysDictDataServiceImpl implements SysDictDataService {

    /** 字典数据仓储。 */
    private final SysDictDataRepository sysDictDataRepository;

    public SysDictDataServiceImpl(SysDictDataRepository sysDictDataRepository) {
        this.sysDictDataRepository = sysDictDataRepository;
    }
}
