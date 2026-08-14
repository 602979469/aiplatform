package com.jakt.aiplatform.core.model.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 在线用户分页查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthOnlineQueryParam extends PageParam {

    /** 用户名/昵称关键字。 */
    private String keyword;
}
