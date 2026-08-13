package com.jakt.aiplatform.common.integration.deepseek.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发送给 DeepSeek 的对话消息（角色 + 内容）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeepSeekChatMessage {

    /** 角色：system / user / assistant。 */
    private String role;

    /** 内容。 */
    private String content;
}
