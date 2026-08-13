package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 发起对话请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiChatRequest extends BaseRequest {

    /** 会话ID，为空时自动新建会话。 */
    private Long sessionId;

    /** 用户输入内容。 */
    @NotBlank(message = "提问内容不能为空")
    @Size(max = 4000, message = "提问内容不能超过4000个字符")
    private String content;

    /** 重试时携带的失败用户消息ID，为空时新增一条提问。 */
    private Long messageId;
}
