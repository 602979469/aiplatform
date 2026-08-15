package com.jakt.aiplatform.core.model.param;

import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 用户表查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthUserQueryParam extends PageParam {

    /** 主键。 */
    private Long userId;

    /** 登录账号。 */
    private String username;

    /** 用户昵称。 */
    private String nickname;

    /** 邮箱。 */
    private String email;

    /** 头像路径。 */
    private String avatar;

    /** 状态（0启用 1停用）。 */
    private EnableStatusEnum status;

    /** 备注。 */
    private String remark;

    /** 创建时间起。 */
    private LocalDateTime createTimeBegin;

    /** 创建时间止。 */
    private LocalDateTime createTimeEnd;

    /** 更新时间起。 */
    private LocalDateTime updateTimeBegin;

    /** 更新时间止。 */
    private LocalDateTime updateTimeEnd;

}
