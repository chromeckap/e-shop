package com.ecommerce.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class OrderTopicConfig {

    @Bean
    public NewTopic OrderTopic() {
        return TopicBuilder
                .name("order-topic")
                .build();
    }
}
