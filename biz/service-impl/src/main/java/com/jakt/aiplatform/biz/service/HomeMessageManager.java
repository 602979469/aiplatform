package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.HomeMessage;

/**
 * 首页留言板管理：web 层唯一入口，负责编排领域服务与业务日志。
 */
public interface HomeMessageManager {

    /**
     * 发布留言（匿名，身份由客户端 IP 决定）。
     *
     * @param content  留言内容
     * @param clientIp 客户端IP
     * @param createBy 创建者（可为空）
     * @return 新增后的留言；主键已回填
     */
    HomeMessage postMessage(String content, String clientIp, String createBy);

    /**
     * 分页查询留言（最新在前）。
     *
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    PageResult<HomeMessage> pageMessages(int pageNum, int pageSize);
}
