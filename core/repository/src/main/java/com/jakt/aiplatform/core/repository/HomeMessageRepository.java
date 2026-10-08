package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.HomeMessage;

/**
 * 首页留言板留言仓储：封装 Mapper，对外只暴露领域模型。
 */
public interface HomeMessageRepository {

    /**
     * 新增。
     *
     * @param homeMessage 留言
     * @return 新增后的留言；主键已回填
     */
    HomeMessage insert(HomeMessage homeMessage);

    /**
     * 分页查询（最新在前）。
     *
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    PageResult<HomeMessage> findPage(int pageNum, int pageSize);

    /**
     * 查询同一 IP 最近一条留言。
     *
     * @param clientIp 客户端IP
     * @return 最近一条留言；没有返回 null
     */
    HomeMessage findLatestByIp(String clientIp);
}
