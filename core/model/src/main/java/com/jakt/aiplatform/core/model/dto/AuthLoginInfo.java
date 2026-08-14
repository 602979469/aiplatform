package com.jakt.aiplatform.core.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 登录结果 DTO：userId + Sa-Token 凭证（组合对象，非表实体）。
 */
@Data
public class AuthLoginInfo implements Serializable {

    /** 序列化版本号。 */
    @Serial
    private static final long serialVersionUID = 1L;

    /** 用户ID。 */
    private Long userId;

    /** token 名称（请求头名）。 */
    private String tokenName;

    /** token 值。 */
    private String tokenValue;
}
