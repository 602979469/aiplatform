package com.jakt.aiplatform.web.controller;

import com.jakt.aiplatform.biz.service.AiChatManager;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.core.model.domain.AiChatResult;
import com.jakt.aiplatform.core.model.domain.AiChatMessage;
import com.jakt.aiplatform.core.model.domain.AiChatSession;
import com.jakt.aiplatform.web.assembler.AiChatAssembler;
import com.jakt.aiplatform.web.checker.AiChatParamChecker;
import com.jakt.aiplatform.web.param.AiChatRequest;
import com.jakt.aiplatform.web.param.AiChatSessionRenameRequest;
import com.jakt.aiplatform.web.result.AiChatMessageResponse;
import com.jakt.aiplatform.web.result.AiChatResultResponse;
import com.jakt.aiplatform.web.result.AiChatSessionResponse;
import com.jakt.aiplatform.web.result.ApiResult;
import com.jakt.aiplatform.web.template.ApiTemplate;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * AI 对话接口：会话管理 + 消息记录 + 发起对话。
 */
@RestController
@RequestMapping("/ai/chat")
@Tag(name = "AI对话")
public class AiChatController {

    private final AiChatManager aiChatManager;

    public AiChatController(AiChatManager aiChatManager) {
        this.aiChatManager = aiChatManager;
    }

    /**
     * 查询当前用户会话列表。
     *
     * @return 会话列表
     */
    @GetMapping("/session/list")
    public ApiResult<List<AiChatSessionResponse>> listSessions() {
        return ApiTemplate.execute(null, new ApiTemplate.Callback<Object, List<AiChatSessionResponse>>() {

            @Override
            public List<AiChatSessionResponse> execute(Object param) {
                List<AiChatSession> sessions = aiChatManager.listSessions();
                return ConvertUtil.map(sessions, AiChatAssembler::toSessionResponse);
            }
        });
    }

    /**
     * 新建会话。
     *
     * @return 新建后的会话
     */
    @PostMapping("/session")
    public ApiResult<AiChatSessionResponse> createSession() {
        return ApiTemplate.execute(null, new ApiTemplate.Callback<Object, AiChatSessionResponse>() {

            @Override
            public AiChatSessionResponse execute(Object param) {
                AiChatSession session = aiChatManager.createSession();
                return AiChatAssembler.toSessionResponse(session);
            }
        });
    }

    /**
     * 修改会话标题。
     *
     * @param request 会话标题请求
     * @return 统一返回体
     */
    @PutMapping("/session")
    public ApiResult<Void> renameSession(@RequestBody AiChatSessionRenameRequest request) {
        return ApiTemplate.executeWithoutResult(request,
                new ApiTemplate.CallbackWithoutResult<AiChatSessionRenameRequest>() {

                    @Override
                    public void beforeService(AiChatSessionRenameRequest param) {
                        AiChatParamChecker.checkRename(param);
                    }

                    @Override
                    public void execute(AiChatSessionRenameRequest param) {
                        aiChatManager.renameSession(param.getSessionId(), param.getSessionName());
                    }
                });
    }

    /**
     * 删除会话（连同消息）。
     *
     * @param sessionId 会话ID
     * @return 统一返回体
     */
    @DeleteMapping("/session/{sessionId}")
    public ApiResult<Void> deleteSession(@PathVariable Long sessionId) {
        return ApiTemplate.executeWithoutResult(sessionId,
                new ApiTemplate.CallbackWithoutResult<Long>() {

                    @Override
                    public void beforeService(Long param) {
                        AiChatParamChecker.checkSessionId(param);
                    }

                    @Override
                    public void execute(Long param) {
                        aiChatManager.deleteSession(param);
                    }
                });
    }

    /**
     * 查询会话消息记录。
     *
     * @param sessionId 会话ID
     * @return 消息列表
     */
    @GetMapping("/message/list")
    public ApiResult<List<AiChatMessageResponse>> listMessages(@RequestParam Long sessionId) {
        return ApiTemplate.execute(sessionId,
                new ApiTemplate.Callback<Long, List<AiChatMessageResponse>>() {

                    @Override
                    public void beforeService(Long param) {
                        AiChatParamChecker.checkSessionId(param);
                    }

                    @Override
                    public List<AiChatMessageResponse> execute(Long param) {
                        List<AiChatMessage> messages = aiChatManager.listMessages(param);
                        return ConvertUtil.map(messages, AiChatAssembler::toMessageResponse);
                    }
                });
    }

    /**
     * 发起对话（保存用户消息，调用模型，返回完整回答）。
     *
     * @param request 对话请求
     * @return 对话结果
     */
    @PostMapping("/message")
    public ApiResult<AiChatResultResponse> chat(@RequestBody AiChatRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<AiChatRequest, AiChatResultResponse>() {

            @Override
            public void beforeService(AiChatRequest param) {
                AiChatParamChecker.checkChat(param);
            }

            @Override
            public AiChatResultResponse execute(AiChatRequest param) {
                AiChatResult result = aiChatManager.chat(
                        param.getSessionId(), param.getMessageId(), param.getContent());
                return AiChatAssembler.toResultResponse(result);
            }
        });
    }
}
