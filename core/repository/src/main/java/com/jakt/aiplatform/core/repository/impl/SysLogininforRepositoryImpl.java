package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.SysLogininforDO;
import com.jakt.aiplatform.common.dal.mapper.SysLogininforMapper;
import com.jakt.aiplatform.common.util.tools.ListUtil;
import com.jakt.aiplatform.core.model.domain.SysLogininfor;
import com.jakt.aiplatform.core.repository.SysLogininforRepository;
import com.jakt.aiplatform.core.repository.convertor.SysLogininforConvertor;
import cn.hutool.core.convert.Convert;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 登录日志仓储实现（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
@Repository
public class SysLogininforRepositoryImpl implements SysLogininforRepository {

    /** 登录日志 Mapper。 */
    private final SysLogininforMapper sysLogininforMapper;

    public SysLogininforRepositoryImpl(SysLogininforMapper sysLogininforMapper) {
        this.sysLogininforMapper = sysLogininforMapper;
    }

    @Override
    public int insertLogininfor(SysLogininfor logininfor) {
        return sysLogininforMapper.insert(SysLogininforConvertor.toDO(logininfor));
    }

    @Override
    public List<SysLogininfor> selectLogininforList(SysLogininfor logininfor) {
        List<SysLogininforDO> list = sysLogininforMapper.selectList(SysLogininforConvertor.toQueryParam(logininfor));
        return ListUtil.convert(list, SysLogininforConvertor::toModel);
    }

    @Override
    public int deleteLogininforByIds(String ids) {
        return sysLogininforMapper.deleteByIds(Convert.toLongArray(ids));
    }

    @Override
    public int cleanLogininfor() {
        return sysLogininforMapper.cleanLogininfor();
    }
}
