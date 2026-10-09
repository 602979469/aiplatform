package com.jakt.aiplatform.core.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

/**
 * DIFY 能力配置：能力入参 → Dify 工作流输入变量的映射，以及输出变量的选取。
 *
 * <p>示例：{@code {"inputVariable":"topic","outputVariable":"report","fixedInputs":{"audience":"普通读者","count":3}}}
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DifyCapabilityConfig {

    /** Dify 应用的 API Key（app-xxx）；留空则用配置 ai.dify.api-key。 */
    private String apiKey;

    /** 能力入参（字符串）映射到工作流的哪个输入变量，默认 input。 */
    private String inputVariable = "input";

    /** 除入参外固定传入的工作流变量（如 audience/count 这类必填项）。 */
    private Map<String, Object> fixedInputs;

    /** 从工作流输出里取哪个变量作为返回值；留空则取第一个输出。 */
    private String outputVariable;
}
