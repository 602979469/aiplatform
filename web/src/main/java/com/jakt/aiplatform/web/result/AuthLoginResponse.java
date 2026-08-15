package com.jakt.aiplatform.web.result;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
/**
 * 登录响应：token 凭证。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AuthLoginResponse extends BaseResult {
    /** token 名称（请求头名）。 */
    private String tokenName;
    /** token 值。 */
    private String tokenValue;
    /** 用户ID。 */
    private Long userId;
}
