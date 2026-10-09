package com.jakt.aiplatform.web.controller;

import com.jakt.aiplatform.biz.service.AiCapabilityManager;
import com.jakt.aiplatform.web.checker.AiCapabilityParamChecker;
import com.jakt.aiplatform.web.param.AiCapabilityInvokeRequest;
import com.jakt.aiplatform.web.result.ApiResult;
import com.jakt.aiplatform.web.template.ApiTemplate;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI 能力接口：按场景码 + 能力码调用能力表里配置的能力（自检、调试、后台工具用）。
 *
 * <p>需要登录；业务链路一般不直接打这个接口，而是在 core-service 里注入 {@code AiCapabilityService}。
 */
@RestController
@RequestMapping("/ai/capabilities")
@Tag(name = "AI 能力")
public class AiCapabilityController {

    /** AI 能力 Manager。 */
    private final AiCapabilityManager aiCapabilityManager;

    public AiCapabilityController(AiCapabilityManager aiCapabilityManager) {
        this.aiCapabilityManager = aiCapabilityManager;
    }

    /**
     * 调用一个 AI 能力（DeepSeek 直连或 Dify 工作流，由能力表 provider 决定）。
     *
     * @param request 调用请求
     * @return 能力返回值
     */
    @PostMapping("/invoke")
    public ApiResult<String> invoke(@RequestBody AiCapabilityInvokeRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<>() {

            @Override
            public void beforeService(AiCapabilityInvokeRequest param) {
                AiCapabilityParamChecker.checkInvokeRequest(param);
            }

            @Override
            public String execute(AiCapabilityInvokeRequest param) {
                return aiCapabilityManager.invoke(param.getSceneCode(), param.getCapabilityCode(),
                        param.getInput());
            }
        });
    }
}
