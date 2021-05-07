package de.kreuzwerker.blogs.kafkaconsumertesting.config;

import de.kreuzwerker.blogs.kafkaconsumertesting.user.UserMessageConsumer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfig {
    @Bean
    @ConditionalOnProperty(value = "messaging.enabled", havingValue = "true")
    public UserMessageConsumer userMessageConsumer() {
        return new UserMessageConsumer();
    }

}
