package com.jakt.aiplatform.common.dal.config;

import com.jakt.aiplatform.core.model.template.AiPlatformTransactionTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 事务配置：将 core-model 的 {@link AiPlatformTransactionTemplate} 装配为 Bean，
 * 事务执行能力由 Spring {@link TransactionTemplate} 提供（默认回滚 RuntimeException）。
 */
@Configuration
public class AiPlatformTransactionConfig {

    @Bean
    public AiPlatformTransactionTemplate aiPlatformTransactionTemplate(PlatformTransactionManager transactionManager) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        return new AiPlatformTransactionTemplate(new AiPlatformTransactionTemplate.TransactionExecutor() {

            @Override
            public <T> T execute(AiPlatformTransactionTemplate.TransactionAction<T> action) {
                return transactionTemplate.execute(status -> action.run());
            }
        });
    }
}
