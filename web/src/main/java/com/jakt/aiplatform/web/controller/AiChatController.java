package com.jakt.aiplatform.web.controller;

import com.jakt.aiplatform.biz.service.AiChatManager;
import com.jakt.aiplatform.web.assembler.AiChatAssembler;
import com.jakt.aiplatform.web.checker.AiChatParamChecker;
import com.jakt.aiplatform.web.param.AiChatRequest;
import com.jakt.aiplatform.web.param.AiChatSessionRenameRequest;
import com.jakt.aiplatform.web.result.AiChatMessageResponse;
import com.jakt.aiplatform.web.result.AiChatResultResponse;
import com.jakt.aiplatform.web.result.AiChatSessionResponse;
import com.jakt.aiplatform.web.result.AiPlatformResult;
import com.jakt.aiplatform.web.template.AiPlatformTemplate;
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
    public AiPlatformResult<List<AiChatSessionResponse>> listSessions() {
        return AiPlatformTemplate.execute(null, new AiPlatformTemplate.Callback<Object, List<AiChatSessionResponse>>() {

            @Override
            public void beforeService(Object param) {
            }

            @Override
            public List<AiChatSessionResponse> execute(Object param) {
                return AiChatAssembler.toSessionResponseList(aiChatManager.listSessions());
            }
        });
    }

    /**
     * 新建会话。
     *
     * @return 新建后的会话
     */
    @PostMapping("/session")
    public AiPlatformResult<AiChatSessionResponse> createSession() {
        return AiPlatformTemplate.execute(null, new AiPlatformTemplate.Callback<Object, AiChatSessionResponse>() {

            @Override
            public void beforeService(Object param) {
            }

            @Override
            public AiChatSessionResponse execute(Object param) {
                return AiChatAssembler.toSessionResponse(aiChatManager.createSession());
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
    public AiPlatformResult<Void> renameSession(@RequestBody AiChatSessionRenameRequest request) {
        return AiPlatformTemplate.executeWithoutResult(request,
                new AiPlatformTemplate.CallbackWithoutResult<AiChatSessionRenameRequest>() {

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
    public AiPlatformResult<Void> deleteSession(@PathVariable Long sessionId) {
        return AiPlatformTemplate.executeWithoutResult(sessionId,
                new AiPlatformTemplate.CallbackWithoutResult<Long>() {

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
    public AiPlatformResult<List<AiChatMessageResponse>> listMessages(@RequestParam Long sessionId) {
        return AiPlatformTemplate.execute(sessionId,
                new AiPlatformTemplate.Callback<Long, List<AiChatMessageResponse>>() {

                    @Override
                    public void beforeService(Long param) {
                        AiChatParamChecker.checkSessionId(param);
                    }

                    @Override
                    public List<AiChatMessageResponse> execute(Long param) {
                        return AiChatAssembler.toMessageResponseList(aiChatManager.listMessages(param));
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
    public AiPlatformResult<AiChatResultResponse> chat(@RequestBody AiChatRequest request) {
        return AiPlatformTemplate.execute(request, new AiPlatformTemplate.Callback<AiChatRequest, AiChatResultResponse>() {

            @Override
            public void beforeService(AiChatRequest param) {
                AiChatParamChecker.checkChat(param);
            }

            @Override
            public AiChatResultResponse execute(AiChatRequest param) {
                return AiChatAssembler.toResultResponse(
                        aiChatManager.chat(param.getSessionId(), param.getMessageId(), param.getContent()));
            }
        });
    }
}
