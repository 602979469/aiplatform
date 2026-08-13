package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysUserOnlineDO;
import com.jakt.aiplatform.common.dal.mapper.SysUserOnlineMapper;
import com.jakt.aiplatform.common.util.tools.ListUtil;
import com.jakt.aiplatform.core.model.domain.SysUserOnline;
import com.jakt.aiplatform.core.repository.SysUserOnlineRepository;
import com.jakt.aiplatform.core.repository.convertor.SysUserOnlineConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 在线用户仓储实现（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
@Repository
public class SysUserOnlineRepositoryImpl implements SysUserOnlineRepository {

    /** 在线用户 Mapper。 */
    private final SysUserOnlineMapper sysUserOnlineMapper;

    public SysUserOnlineRepositoryImpl(SysUserOnlineMapper sysUserOnlineMapper) {
        this.sysUserOnlineMapper = sysUserOnlineMapper;
    }

    @Override
    public SysUserOnline selectOnlineById(String sessionId) {
        return SysUserOnlineConvertor.toModel(sysUserOnlineMapper.selectById(sessionId));
    }

    @Override
    public int deleteOnlineById(String sessionId) {
        return sysUserOnlineMapper.deleteById(sessionId);
    }

    @Override
    public int saveOnline(SysUserOnline online) {
        return sysUserOnlineMapper.saveOnline(SysUserOnlineConvertor.toDO(online));
    }

    @Override
    public List<SysUserOnline> selectUserOnlineList(SysUserOnline userOnline) {
        List<SysUserOnlineDO> list = sysUserOnlineMapper.selectUserOnlineList(SysUserOnlineConvertor.toQueryParam(userOnline));
        return ListUtil.convert(list, SysUserOnlineConvertor::toModel);
    }

    @Override
    public List<SysUserOnline> selectOnlineByExpired(String lastAccessTime) {
        List<SysUserOnlineDO> list = sysUserOnlineMapper.selectOnlineByExpired(lastAccessTime);
        return ListUtil.convert(list, SysUserOnlineConvertor::toModel);
    }
}
