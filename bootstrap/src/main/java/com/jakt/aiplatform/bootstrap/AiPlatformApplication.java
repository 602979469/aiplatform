package com.jakt.aiplatform.bootstrap;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 应用启动入口。扫描范围覆盖全部模块。
 */
@SpringBootApplication(scanBasePackages = "com.jakt.aiplatform")
@ConfigurationPropertiesScan(basePackages = "com.jakt.aiplatform")
@MapperScan("com.jakt.aiplatform.common.dal.mapper")
@EnableAsync
@EnableRetry
public class AiPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiPlatformApplication.class, args);
    }
}
