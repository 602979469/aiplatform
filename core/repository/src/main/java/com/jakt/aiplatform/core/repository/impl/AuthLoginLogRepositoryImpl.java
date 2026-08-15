package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.common.dal.dataobject.AuthLoginLogDO;
import com.jakt.aiplatform.common.dal.mapper.AuthLoginLogMapper;
import com.jakt.aiplatform.common.util.tools.AssertUtil;
import com.jakt.aiplatform.core.model.domain.AuthLoginLog;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.util.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.AuthLoginLogQueryParam;
import com.jakt.aiplatform.common.util.result.PageResult;
import com.jakt.aiplatform.common.util.tools.LoggerUtil;
import com.jakt.aiplatform.core.repository.AuthLoginLogRepository;
import com.jakt.aiplatform.core.repository.convertor.AuthLoginLogConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 登录记录表仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
@Repository
public class AuthLoginLogRepositoryImpl implements AuthLoginLogRepository {

    /** 登录记录表 Mapper。 */
    private final AuthLoginLogMapper authLoginLogMapper;

    public AuthLoginLogRepositoryImpl(AuthLoginLogMapper authLoginLogMapper) {
        this.authLoginLogMapper = authLoginLogMapper;
    }

    @Override
    public AuthLoginLog findById(Long id) {
        AuthLoginLogDO logDO = authLoginLogMapper.selectById(id);
        return AuthLoginLogConvertor.toModel(logDO);
    }

    @Override
    public PageResult<AuthLoginLog> findPage(AuthLoginLogQueryParam query) {
        com.jakt.aiplatform.common.dal.query.AuthLoginLogDalQuery dalQuery = AuthLoginLogConvertor.toDalQuery(query);
        List<AuthLoginLogDO> doList = authLoginLogMapper.selectPage(dalQuery);
        long total = authLoginLogMapper.countByQuery(dalQuery);
        List<AuthLoginLog> list = ConvertUtil.map(doList, AuthLoginLogConvertor::toModel);
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public AuthLoginLog insert(AuthLoginLog authLoginLog) {
        AuthLoginLogDO authLoginLogDO = AuthLoginLogConvertor.toDO(authLoginLog);
        authLoginLogMapper.insert(authLoginLogDO);
        // 主键回填到入参（自增主键由数据库生成），调用方直接使用原对象
        authLoginLog.setLogId(authLoginLogDO.getLogId());
        return authLoginLog;
    }

    @Override
    public int deleteById(Long id) {
        return authLoginLogMapper.deleteById(id);
    }
}
