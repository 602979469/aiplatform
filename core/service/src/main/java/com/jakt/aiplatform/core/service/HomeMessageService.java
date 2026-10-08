package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.HomeMessage;

/**
 * 首页留言板领域服务：承载匿名身份（IP 归属）、发帖频率与内容规则。
 */
public interface HomeMessageService {

    /** 头像素材所在的文件命名空间（file_info.namespace）。 */
    String AVATAR_NAMESPACE = "avatar";

    /** 留言内容最大长度。 */
    int MAX_CONTENT_LENGTH = 200;

    /** 气泡配色数量（与前端调色板一一对应）。 */
    int COLOR_COUNT = 12;

    /** 同一 IP 连续发帖最小间隔（秒）。 */
    int POST_INTERVAL_SECONDS = 10;

    /** 头像缺失时的兜底昵称。 */
    String ANONYMOUS_NAME = "匿名";

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
    PageResult<HomeMessage> findPage(int pageNum, int pageSize);
}
