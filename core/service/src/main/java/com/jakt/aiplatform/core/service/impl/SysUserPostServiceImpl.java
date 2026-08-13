package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.repository.SysUserPostRepository;
import com.jakt.aiplatform.core.service.SysUserPostService;
import org.springframework.stereotype.Service;

/**
 * SysUserPost 领域服务实现（RuoYi 移植过渡：方法清空，构造器保留待嵌入 RuoYi service 逻辑）。
 */
@Service
public class SysUserPostServiceImpl implements SysUserPostService {

    /** SysUserPost 仓储。 */
    private final SysUserPostRepository userpostRepository;

    public SysUserPostServiceImpl(SysUserPostRepository userpostRepository) {
        this.userpostRepository = userpostRepository;
    }
}
