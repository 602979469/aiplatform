package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.repository.SysDictTypeRepository;
import com.jakt.aiplatform.core.service.SysDictTypeService;
import org.springframework.stereotype.Service;

/**
 * 字典类型领域服务实现（RuoYi 移植过渡：方法清空，构造器保留待嵌入 RuoYi service 逻辑）。
 */
@Service
public class SysDictTypeServiceImpl implements SysDictTypeService {

    /** 字典类型仓储。 */
    private final SysDictTypeRepository sysDictTypeRepository;

    public SysDictTypeServiceImpl(SysDictTypeRepository sysDictTypeRepository) {
        this.sysDictTypeRepository = sysDictTypeRepository;
    }
}
