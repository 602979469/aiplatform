package com.jakt.aiplatform.web.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 留言板发布留言请求：匿名留言只需要内容，身份由服务端按客户端 IP 分配。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HomeMessageCreateRequest extends BaseRequest {

    /** 留言内容（最多 200 字）。 */
    private String content;
}
