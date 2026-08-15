package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 修改会话标题请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiChatSessionRenameRequest extends BaseRequest {

    /** 会话ID。 */
    @NotNull(message = "会话ID不能为空")
    private Long sessionId;

    /** 会话标题。 */
    @NotBlank(message = "会话标题不能为空")
    @Size(max = 100, message = "会话标题不能超过100个字符")
    private String sessionName;
}
