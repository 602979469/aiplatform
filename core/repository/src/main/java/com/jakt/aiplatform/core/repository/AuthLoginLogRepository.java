package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.AuthLoginLog;
import com.jakt.aiplatform.core.model.param.AuthLoginLogQueryParam;
import com.jakt.aiplatform.common.util.result.PageResult;

/**
 * 登录记录表仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
public interface AuthLoginLogRepository {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 登录记录表领域模型
     */
    AuthLoginLog findById(Long id);

    /**
     * 分页查询。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<AuthLoginLog> findPage(AuthLoginLogQueryParam query);

    /**
     * 新增。
     *
     * @param authLoginLog 登录记录表
     * @return 新增后的登录记录表；主键已回填到入参，返回同一对象
     */
    AuthLoginLog insert(AuthLoginLog authLoginLog);

    /**
     * 按主键删除。
     *
     * @param id 主键
     */
    int deleteById(Long id);
}
