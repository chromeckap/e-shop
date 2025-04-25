package com.ecommerce.delivery;

import com.ecommerce.deliverymethod.DeliveryMethod;
import jakarta.persistence.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeliveryTest {

    @Test
    void testNoArgsConstructor() {
        // Arrange & Act
        Delivery delivery = new Delivery();

        // Assert
        assertNull(delivery.getId());
        assertNull(delivery.getOrderId());
        assertNull(delivery.getStatus());
        assertNull(delivery.getMethod());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        Long id = 1L;
        Long orderId = 100L;
        DeliveryStatus status = DeliveryStatus.CREATED;
        DeliveryMethod method = mock(DeliveryMethod.class);

        // Act
        Delivery delivery = new Delivery(id, orderId, status, method);

        // Assert
        assertEquals(id, delivery.getId());
        assertEquals(orderId, delivery.getOrderId());
        assertEquals(status, delivery.getStatus());
        assertEquals(method, delivery.getMethod());
    }

    @Test
    void testBuilder() {
        // Arrange
        Long id = 1L;
        Long orderId = 100L;
        DeliveryStatus status = DeliveryStatus.CREATED;
        DeliveryMethod method = mock(DeliveryMethod.class);

        // Act
        Delivery delivery = Delivery.builder()
                .id(id)
                .orderId(orderId)
                .status(status)
                .method(method)
                .build();

        // Assert
        assertEquals(id, delivery.getId());
        assertEquals(orderId, delivery.getOrderId());
        assertEquals(status, delivery.getStatus());
        assertEquals(method, delivery.getMethod());
    }

    @Test
    void testSetters() {
        // Arrange
        Delivery delivery = new Delivery();
        Long id = 1L;
        Long orderId = 100L;
        DeliveryStatus status = DeliveryStatus.CREATED;
        DeliveryMethod method = mock(DeliveryMethod.class);

        // Act
        delivery.setId(id);
        delivery.setOrderId(orderId);
        delivery.setStatus(status);
        delivery.setMethod(method);

        // Assert
        assertEquals(id, delivery.getId());
        assertEquals(orderId, delivery.getOrderId());
        assertEquals(status, delivery.getStatus());
        assertEquals(method, delivery.getMethod());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        Delivery delivery1 = new Delivery();
        delivery1.setId(1L);

        Delivery delivery2 = new Delivery();
        delivery2.setId(1L);

        Delivery delivery3 = new Delivery();
        delivery3.setId(2L);

        // Assert
        assertEquals(delivery1, delivery2);
        assertNotEquals(delivery1, delivery3);
        assertEquals(delivery1.hashCode(), delivery2.hashCode());
        assertNotEquals(delivery1.hashCode(), delivery3.hashCode());
    }

    @Test
    void testAnnotations() {
        // Verify JPA annotations
        assertNotNull(Delivery.class.getAnnotation(Entity.class));
        assertNotNull(Delivery.class.getAnnotation(Table.class));

        try {
            // Check ID field
            var idField = Delivery.class.getDeclaredField("id");
            assertNotNull(idField.getAnnotation(Id.class));
            assertNotNull(idField.getAnnotation(GeneratedValue.class));

            // Check orderId field
            var orderIdField = Delivery.class.getDeclaredField("orderId");
            assertNotNull(orderIdField.getAnnotation(Column.class));

            // Check status field
            var statusField = Delivery.class.getDeclaredField("status");
            assertNotNull(statusField.getAnnotation(Enumerated.class));
            assertNotNull(statusField.getAnnotation(Column.class));

            // Check method field
            var methodField = Delivery.class.getDeclaredField("method");
            assertNotNull(methodField.getAnnotation(ManyToOne.class));
            assertNotNull(methodField.getAnnotation(JoinColumn.class));
        } catch (NoSuchFieldException e) {
            fail("Expected field not found", e);
        }
    }

}