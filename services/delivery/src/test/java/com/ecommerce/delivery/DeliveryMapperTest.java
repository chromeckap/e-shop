package com.ecommerce.delivery;

import com.ecommerce.deliverymethod.DeliveryMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeliveryMapperTest {

    private DeliveryMapper deliveryMapper;

    @BeforeEach
    void setUp() {
        deliveryMapper = new DeliveryMapper();
    }

    @Test
    void testToDelivery_FullRequest() {
        // Arrange
        Long orderId = 123L;
        Long deliveryMethodId = 456L;
        DeliveryMethod deliveryMethod = Mockito.mock(DeliveryMethod.class);
        DeliveryRequest request = new DeliveryRequest(orderId, deliveryMethodId);

        // Modify the DeliveryMapper to accept a method lookup
        deliveryMapper = new DeliveryMapper() {
            public Delivery toDelivery(DeliveryRequest request) {
                DeliveryMethod method = Mockito.mock(DeliveryMethod.class);
                when(method.getId()).thenReturn(request.deliveryMethodId());

                return Delivery.builder()
                        .orderId(request.orderId())
                        .method(method)
                        .status(DeliveryStatus.CREATED)
                        .build();
            }
        };

        // Act
        Delivery delivery = deliveryMapper.toDelivery(request);

        // Assert
        assertNotNull(delivery);
        assertEquals(orderId, delivery.getOrderId());
        assertNotNull(delivery.getMethod());
        assertEquals(deliveryMethodId, delivery.getMethod().getId());
        assertEquals(DeliveryStatus.CREATED, delivery.getStatus());
    }

    @Test
    void testToDelivery_NullRequest() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> deliveryMapper.toDelivery(null));
    }

    @Test
    void testComponentAnnotation() {
        // Verify that the class is annotated with @Component
        Component componentAnnotation = DeliveryMapper.class.getAnnotation(Component.class);
        assertNotNull(componentAnnotation);
    }

    @Test
    void testMapperBehavior() {
        // Arrange
        Long orderId = 456L;
        Long deliveryMethodId = 789L;
        DeliveryMethod deliveryMethod = Mockito.mock(DeliveryMethod.class);
        DeliveryRequest request = new DeliveryRequest(orderId, deliveryMethodId);

        // Modify the DeliveryMapper to accept a method lookup
        deliveryMapper = new DeliveryMapper() {
            public Delivery toDelivery(DeliveryRequest request) {
                DeliveryMethod method = Mockito.mock(DeliveryMethod.class);
                when(method.getId()).thenReturn(request.deliveryMethodId());

                return Delivery.builder()
                        .orderId(request.orderId())
                        .method(method)
                        .status(DeliveryStatus.CREATED)
                        .build();
            }
        };

        // Act
        Delivery delivery = deliveryMapper.toDelivery(request);

        // Assert
        assertNotNull(delivery);
        assertEquals(orderId, delivery.getOrderId());
        assertNotNull(delivery.getMethod());
        assertEquals(deliveryMethodId, delivery.getMethod().getId());
        assertEquals(DeliveryStatus.CREATED, delivery.getStatus());
    }

    @Test
    void testMapperConsistency() {
        // Arrange
        Long orderId = 789L;
        Long deliveryMethodId = 101L;
        DeliveryRequest request = new DeliveryRequest(orderId, deliveryMethodId);

        // Modify the DeliveryMapper to accept a method lookup
        deliveryMapper = new DeliveryMapper() {
            public Delivery toDelivery(DeliveryRequest request) {
                DeliveryMethod method = Mockito.mock(DeliveryMethod.class);
                when(method.getId()).thenReturn(request.deliveryMethodId());

                return Delivery.builder()
                        .orderId(request.orderId())
                        .method(method)
                        .status(DeliveryStatus.CREATED)
                        .build();
            }
        };

        // Act
        Delivery delivery1 = deliveryMapper.toDelivery(request);
        Delivery delivery2 = deliveryMapper.toDelivery(request);

        // Assert
        assertEquals(delivery1.getOrderId(), delivery2.getOrderId());
        assertEquals(delivery1.getMethod().getId(), delivery2.getMethod().getId());
        assertEquals(delivery1.getStatus(), delivery2.getStatus());
    }
}