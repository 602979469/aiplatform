package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysDictTypeManager;
import com.jakt.aiplatform.core.service.SysDictTypeService;
import org.springframework.stereotype.Service;

/**
 * 字典类型管理实现类（RuoYi 移植过渡：方法清空，构造器保留待用例编排嵌入）。
 */
@Service
public class SysDictTypeManagerImpl implements SysDictTypeManager {

    /** 字典类型领域服务。 */
    private final SysDictTypeService sysDictTypeService;

    public SysDictTypeManagerImpl(SysDictTypeService sysDictTypeService) {
        this.sysDictTypeService = sysDictTypeService;
    }
}
