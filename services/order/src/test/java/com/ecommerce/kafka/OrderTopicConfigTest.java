package com.ecommerce.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class OrderTopicConfigTest {

    @InjectMocks
    private OrderTopicConfig orderTopicConfig;

    @Test
    void orderTopic_ShouldCreateNewTopic() {
        // When
        NewTopic topic = orderTopicConfig.OrderTopic();

        // Then
        assertNotNull(topic);
        assertEquals("order-topic", topic.name());
        // Additional assertions could be made if the topic config had more properties
        // such as partitions, replicas, etc.
    }
}
