package com.jakt.aiplatform.web.result;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.List;
/**
 * 当前用户信息响应：用户 + 角色 + 权限码。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AuthUserInfoResponse extends BaseResult {
    /** 用户。 */
    private AuthUserResponse user;
    /** 角色标识集合。 */
    private List<String> roles;
    /** 权限码集合。 */
    private List<String> perms;
}
