package com.ecommerce.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * This is an integration test that requires Spring context, but we can mock KafkaAdmin.
 * Alternatively, we could test this with a simple unit test if needed.
 */
@SpringBootTest(classes = PaymentTopicConfig.class)
class PaymentTopicConfigTest {

    @Autowired
    private PaymentTopicConfig topicConfig;

    @Test
    void paymentTopic_ShouldCreateTopicWithCorrectName() {
        // Act
        NewTopic topic = topicConfig.paymentTopic();

        // Assert
        assertNotNull(topic);
        assertEquals("payment-create-topic", topic.name());
        // If we want to test other properties like partitions, replication factor, etc.
        // we can add more assertions here
    }
}