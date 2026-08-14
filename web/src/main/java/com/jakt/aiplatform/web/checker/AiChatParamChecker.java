package com.jakt.aiplatform.web.checker;

import com.jakt.aiplatform.common.util.tools.AssertUtil;
import com.jakt.aiplatform.common.util.tools.ParamValidator;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.web.param.AiChatRequest;
import com.jakt.aiplatform.web.param.AiChatSessionRenameRequest;

/**
 * AI 对话参数检查器。
 */
public class AiChatParamChecker {

    private AiChatParamChecker() {
    }

    /**
     * 检查发起对话参数。
     *
     * @param request 对话请求
     */
    public static void checkChat(AiChatRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "对话参数不能为空");
        ParamValidator.validate(request);
    }

    /**
     * 检查修改会话标题参数。
     *
     * @param request 会话标题请求
     */
    public static void checkRename(AiChatSessionRenameRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "会话标题参数不能为空");
        ParamValidator.validate(request);
    }

    /**
     * 检查会话 ID 参数。
     *
     * @param sessionId 会话ID
     */
    public static void checkSessionId(Long sessionId) {
        AssertUtil.throwErrWhenNull(sessionId, ErrorCodeEnum.PARAM_INVALID, "会话ID不能为空");
    }
}
