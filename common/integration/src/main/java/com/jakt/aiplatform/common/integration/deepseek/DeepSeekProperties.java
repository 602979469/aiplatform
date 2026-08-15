package com.jakt.aiplatform.common.integration.deepseek;

import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.common.util.enums.LogFileEnum;
import com.jakt.aiplatform.common.util.tools.LoggerUtil;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DeepSeek 配置。
 *
 * <p>API Key 优先级：配置文件 ai.deepseek.api-key &gt; 环境变量 DEEPSEEK_API_KEY
 * &gt; Codex 配置文件（~/.codex/config.toml 或 ~/.code/config.toml）[model_providers.deepseek] 小节。
 */
@Data
@ConfigurationProperties(prefix = "ai.deepseek")
public class DeepSeekProperties implements InitializingBean {

    /** API Key。 */
    private String apiKey;

    /** 接口地址。 */
    private String baseUrl = "https://api.deepseek.com";

    /** 模型名称（deepseek-chat 为快速非深度思考模型）。 */
    private String model = "deepseek-chat";

    /** 温度。 */
    private Double temperature = 1.0;

    /** 最大输出长度。 */
    private Integer maxTokens = 4096;

    /** 连接超时（秒）。 */
    private Integer connectTimeout = 30;

    /** 读取超时（秒）。 */
    private Integer readTimeout = 180;

    @Override
    public void afterPropertiesSet() {
        if (StrUtil.isBlank(apiKey)) {
            apiKey = System.getenv("DEEPSEEK_API_KEY");
        }
        if (StrUtil.isBlank(apiKey)) {
            CodexConfig config = loadCodexConfig();
            if (config != null) {
                if (StrUtil.isBlank(apiKey)) {
                    apiKey = config.getApiKey();
                }
                if (StrUtil.isNotBlank(config.getBaseUrl())) {
                    baseUrl = config.getBaseUrl();
                }
            }
        }
        if (StrUtil.isBlank(apiKey)) {
            LoggerUtil.warn(LogFileEnum.COMMON_ERROR,
                    "DeepSeek API Key 未配置，请在 application.yml 配置 ai.deepseek.api-key 或设置环境变量 DEEPSEEK_API_KEY");
        }
        if (StrUtil.isNotBlank(baseUrl) && baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "DeepSeek 配置加载完成: baseUrl={}, model={}, apiKey配置={}",
                baseUrl, model, StrUtil.isNotBlank(apiKey));
    }

    /**
     * 读取 Codex 配置文件中的 DeepSeek 配置。
     */
    private CodexConfig loadCodexConfig() {
        String home = System.getProperty("user.home");
        String[] candidates = { home + "/.codex/config.toml", home + "/.code/config.toml" };
        for (String path : candidates) {
            Path file = Paths.get(path);
            if (Files.exists(file)) {
                try {
                    String content = Files.readString(file, StandardCharsets.UTF_8);
                    CodexConfig config = parseTomlSection(content);
                    if (config != null && StrUtil.isNotBlank(config.getApiKey())) {
                        return config;
                    }
                } catch (IOException e) {
                    LoggerUtil.warn(LogFileEnum.COMMON_ERROR, "读取 Codex 配置文件失败: {}", path);
                }
            }
        }
        return null;
    }

    /**
     * 解析 TOML 中 [model_providers.deepseek] 小节（仅读取本模块需要的字段）。
     */
    private CodexConfig parseTomlSection(String content) {
        Matcher section = Pattern.compile(
                "(?ms)^\\s*\\[\\s*model_providers\\.deepseek\\s*\\]\\s*(.*?)(?=^\\s*\\[|\\z)").matcher(content);
        if (!section.find()) {
            return null;
        }
        String body = section.group(1);
        CodexConfig config = new CodexConfig();
        config.setApiKey(readTomlValue(body, "experimental_bearer_token"));
        if (StrUtil.isBlank(config.getApiKey())) {
            config.setApiKey(readTomlValue(body, "api_key"));
        }
        config.setBaseUrl(readTomlValue(body, "base_url"));
        return config;
    }

    /**
     * 读取 TOML 小节内指定 key 的字符串值。
     */
    private String readTomlValue(String body, String key) {
        Matcher matcher = Pattern.compile("(?m)^\\s*" + Pattern.quote(key) + "\\s*=\\s*\"([^\"]*)\"").matcher(body);
        return matcher.find() ? matcher.group(1) : null;
    }

    /**
     * Codex 配置文件中 DeepSeek 小节内容。
     */
    private static class CodexConfig {

        private String apiKey;

        private String baseUrl;

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
    }
}
