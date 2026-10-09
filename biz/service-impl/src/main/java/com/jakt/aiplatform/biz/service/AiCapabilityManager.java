package com.jakt.aiplatform.biz.service;

/**
 * AI 能力管理：给 web 层一个按场景码/能力码调用能力的入口（自检、调试、后台工具用）。
 */
public interface AiCapabilityManager {

    /**
     * 调用一个 AI 能力。
     *
     * @param sceneCode 场景码
     * @param capabilityCode 能力码
     * @param input 能力入参
     * @return 能力返回值（DeepSeek 的回答 / Dify 工作流输出）
     */
    String invoke(String sceneCode, String capabilityCode, String input);
}
