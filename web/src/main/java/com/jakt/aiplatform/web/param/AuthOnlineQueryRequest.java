package com.jakt.aiplatform.web.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 在线用户分页查询请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthOnlineQueryRequest extends PageQueryRequest {

    /** 用户名/昵称关键字。 */
    private String keyword;
}
