package com.jakt.aiplatform.web.result;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 用户AI会话响应 DTO。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiChatSessionResponse extends BaseResult {
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
