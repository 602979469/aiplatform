package com.jakt.aiplatform.core.model.param;

import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户AI会话查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiChatSessionQueryParam extends PageParam {

    /** 主键。 */
    private Long sessionId;

    /** 会话名称。 */
    private String sessionName;

    /** 用户ID。 */
    private Long userId;

    /** 用户名。 */
    private String userName;

    /** 会话状态（0正常 1停用）。 */
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
