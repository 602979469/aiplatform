package com.jakt.aiplatform.web.result;
import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
/**
 * 用户响应（不含密码）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AuthUserResponse extends BaseResult {
    /** 用户ID。 */
    private Long userId;
    /** 登录账号。 */
    private String username;
    /** 昵称。 */
    private String nickname;
    /** 邮箱。 */
    private String email;
    /** 头像路径。 */
    private String avatar;
    /** 状态（0启用 1停用）。 */
    private EnableStatusEnum status;
    /** 备注。 */
    private String remark;
    /** 创建时间。 */
    private LocalDateTime createTime;
    /** 是否超级管理员账号：为 true 时前端禁用修改/删除/状态切换，后端同样拒绝变更。 */
    private Boolean superAdmin;
}
