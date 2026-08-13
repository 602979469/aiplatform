package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysLogininfor;

import java.util.List;

/**
 * 登录日志仓储（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
public interface SysLogininforRepository {

    /**
     * 新增登录日志。
     *
     * @param logininfor 登录日志
     * @return 影响行数
     */
    int insertLogininfor(SysLogininfor logininfor);

    /**
     * 按条件查询登录日志列表。
     *
     * @param logininfor 查询条件（实体即条件）
     * @return 登录日志列表
     */
    List<SysLogininfor> selectLogininforList(SysLogininfor logininfor);

    /**
     * 按 ID 集合批量删除登录日志。
     *
     * @param ids 登录日志ID集合（逗号分隔）
     * @return 影响行数
     */
    int deleteLogininforByIds(String ids);

    /**
     * 清空登录日志。
     *
     * @return 影响行数
     */
    int cleanLogininfor();
}
