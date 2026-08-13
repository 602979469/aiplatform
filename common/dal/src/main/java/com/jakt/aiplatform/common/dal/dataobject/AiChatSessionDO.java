package com.jakt.aiplatform.common.dal.dataobject;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户AI会话 DO对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiChatSessionDO extends BaseDO {
    /** 主键。 */
    private Long sessionId;

    /** 会话名称。 */
    private String sessionName;

    /** 用户ID。 */
    private Long userId;

    /** 用户名。 */
    private String userName;

    /** 会话状态（0正常 1停用）。 */
    private String status;

    /** 备注。 */
    private String remark;

}
