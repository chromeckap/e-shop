package com.ecommerce.kafka;

import com.ecommerce.address.AddressRequest;
import com.ecommerce.feignclient.product.PurchaseResponse;
import com.ecommerce.userdetails.UserDetailsRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderProducerTest {

    @Mock
    private KafkaTemplate<String, OrderConfirmation> kafkaTemplate;

    @InjectMocks
    private OrderProducer orderProducer;

    @Captor
    private ArgumentCaptor<Message<OrderConfirmation>> messageCaptor;

    private OrderConfirmation orderConfirmation;
    private static final String TOPIC_NAME = "order-confirmation-topic";

    @BeforeEach
    void setUp() {
        // Create a sample AddressRequest
        AddressRequest address = new AddressRequest(
                "Main Street",
                "123",
                "New York"
        );

        // Create a sample UserDetailsRequest
        UserDetailsRequest user = new UserDetailsRequest(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                address
        );

        // Create a sample product
        PurchaseResponse product = new PurchaseResponse(
                1L,
                "Sample Product",
                new BigDecimal("99.99"),
                2,
                new BigDecimal("199.98"),
                Map.of("color", "blue", "size", "medium")
        );

        // Create additional costs
        Map<String, BigDecimal> additionalCosts = new HashMap<>();
        additionalCosts.put("shipping", new BigDecimal("10.00"));
        additionalCosts.put("tax", new BigDecimal("20.00"));

        // Create a set of products
        Set<PurchaseResponse> products = new HashSet<>();
        products.add(product);

        // Create the order confirmation
        String currentDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        orderConfirmation = OrderConfirmation.builder()
                .orderId(123L)
                .totalPrice(new BigDecimal("229.98"))
                .user(user)
                .products(products)
                .additionalCosts(additionalCosts)
                .orderDate(currentDate)
                .build();
    }

    @Test
    void sendOrderConfirmation_ShouldSendMessageToKafka() {
        // When
        orderProducer.sendOrderConfirmation(orderConfirmation);

        // Then
        verify(kafkaTemplate, times(1)).send(messageCaptor.capture());

        Message<OrderConfirmation> capturedMessage = messageCaptor.getValue();

        // Verify the message
        assertNotNull(capturedMessage);
        assertEquals(orderConfirmation, capturedMessage.getPayload());
        assertEquals(TOPIC_NAME, capturedMessage.getHeaders().get("kafka_topic"));
    }

    @Test
    void sendOrderConfirmation_ShouldHandleNullOrderConfirmation() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> orderProducer.sendOrderConfirmation(null));
    }
}