package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.repository.SysPostRepository;
import com.jakt.aiplatform.core.service.SysPost;
import org.springframework.stereotype.Service;

/**
 * SysPost 领域服务实现（RuoYi 移植过渡：方法清空，构造器保留待嵌入 RuoYi service 逻辑）。
 */
@Service
public class SysPostServiceImpl implements SysPost {

    /** SysPost 仓储。 */
    private final SysPostRepository postRepository;

    public SysPostServiceImpl(SysPostRepository postRepository) {
        this.postRepository = postRepository;
    }
}
