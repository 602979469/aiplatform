package com.jakt.aiplatform.web.result;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
/**
 * 在线用户响应。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AuthOnlineResponse extends BaseResult {
    /** token 值（脱敏）。 */
    private String tokenValue;
    /** 用户ID。 */
    private Long userId;
    /** 用户名。 */
    private String username;
    /** 昵称。 */
    private String nickname;
    /** 登录IP。 */
    private String loginIp;
    /** 登录时间。 */
    private LocalDateTime loginTime;
}
