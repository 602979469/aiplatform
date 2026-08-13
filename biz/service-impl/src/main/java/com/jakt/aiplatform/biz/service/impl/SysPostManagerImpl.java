package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.SysPostManager;
import com.jakt.aiplatform.core.service.SysPost;
import org.springframework.stereotype.Service;

/**
 * SysPost 管理实现类（RuoYi 移植过渡：方法清空，构造器保留待用例编排嵌入）。
 */
@Service
public class SysPostManagerImpl implements SysPostManager {

    /** SysPost 领域服务。 */
    private final SysPost postService;

    public SysPostManagerImpl(SysPost postService) {
        this.postService = postService;
    }
}
