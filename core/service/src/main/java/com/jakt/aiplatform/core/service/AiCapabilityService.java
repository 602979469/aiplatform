package com.jakt.aiplatform.core.service;

/**
 * AI能力领域服务（内部机制，不对外暴露 Controller）。
 */
public interface AiCapabilityService {

    /**
     * 调用固定 AI 能力：每次调用独立上下文（system 规则 + user 输入 + assistant 回答落库）。
     *
     * @param sceneCode      场景码
     * @param capabilityCode 能力编码
     * @param input          用户输入
     * @return AI 回答
     */
    String invoke(String sceneCode, String capabilityCode, String input);
}
