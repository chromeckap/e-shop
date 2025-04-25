package com.ecommerce.deliverymethod;

import com.ecommerce.delivery.Delivery;
import com.ecommerce.strategy.CourierType;
import jakarta.persistence.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryMethodTest {

    @Test
    void testNoArgsConstructor() {
        // Arrange & Act
        DeliveryMethod deliveryMethod = new DeliveryMethod();

        // Assert
        assertNull(deliveryMethod.getId());
        assertNull(deliveryMethod.getName());
        assertNull(deliveryMethod.getCourierType());
        assertFalse(deliveryMethod.isActive());
        assertNull(deliveryMethod.getPrice());
        assertFalse(deliveryMethod.isFreeForOrderAbove());
        assertNull(deliveryMethod.getFreeForOrderAbove());
        assertNull(deliveryMethod.getDeliveries());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        Long id = 1L;
        String name = "Standard Delivery";
        CourierType courierType = CourierType.PACKETA;
        boolean isActive = true;
        BigDecimal price = BigDecimal.valueOf(5.99);
        boolean isFreeForOrderAbove = true;
        BigDecimal freeForOrderAbove = BigDecimal.valueOf(50.00);
        Set<Delivery> deliveries = new HashSet<>();

        // Act
        DeliveryMethod deliveryMethod = new DeliveryMethod(
                id, name, courierType, isActive, price,
                isFreeForOrderAbove, freeForOrderAbove, deliveries
        );

        // Assert
        assertEquals(id, deliveryMethod.getId());
        assertEquals(name, deliveryMethod.getName());
        assertEquals(courierType, deliveryMethod.getCourierType());
        assertTrue(deliveryMethod.isActive());
        assertEquals(price, deliveryMethod.getPrice());
        assertTrue(deliveryMethod.isFreeForOrderAbove());
        assertEquals(freeForOrderAbove, deliveryMethod.getFreeForOrderAbove());
        assertEquals(deliveries, deliveryMethod.getDeliveries());
    }

    @Test
    void testBuilder() {
        // Arrange
        Long id = 1L;
        String name = "Express Delivery";
        CourierType courierType = CourierType.BALIKOVNA;
        boolean isActive = true;
        BigDecimal price = BigDecimal.valueOf(9.99);
        boolean isFreeForOrderAbove = false;
        BigDecimal freeForOrderAbove = BigDecimal.valueOf(100.00);
        Set<Delivery> deliveries = new HashSet<>();

        // Act
        DeliveryMethod deliveryMethod = DeliveryMethod.builder()
                .id(id)
                .name(name)
                .courierType(courierType)
                .isActive(isActive)
                .price(price)
                .isFreeForOrderAbove(isFreeForOrderAbove)
                .freeForOrderAbove(freeForOrderAbove)
                .deliveries(deliveries)
                .build();

        // Assert
        assertEquals(id, deliveryMethod.getId());
        assertEquals(name, deliveryMethod.getName());
        assertEquals(courierType, deliveryMethod.getCourierType());
        assertTrue(deliveryMethod.isActive());
        assertEquals(price, deliveryMethod.getPrice());
        assertFalse(deliveryMethod.isFreeForOrderAbove());
        assertEquals(freeForOrderAbove, deliveryMethod.getFreeForOrderAbove());
        assertEquals(deliveries, deliveryMethod.getDeliveries());
    }

    @Test
    void testSetters() {
        // Arrange
        DeliveryMethod deliveryMethod = new DeliveryMethod();
        Long id = 1L;
        String name = "Custom Delivery";
        CourierType courierType = CourierType.OTHER;
        boolean isActive = true;
        BigDecimal price = BigDecimal.valueOf(7.50);
        boolean isFreeForOrderAbove = true;
        Set<Delivery> deliveries = new HashSet<>();

        // Act
        deliveryMethod.setId(id);
        deliveryMethod.setName(name);
        deliveryMethod.setCourierType(courierType);
        deliveryMethod.setActive(isActive);
        deliveryMethod.setPrice(price);
        deliveryMethod.setFreeForOrderAbove(isFreeForOrderAbove);
        deliveryMethod.setDeliveries(deliveries);

        // Assert
        assertEquals(id, deliveryMethod.getId());
        assertEquals(name, deliveryMethod.getName());
        assertEquals(courierType, deliveryMethod.getCourierType());
        assertTrue(deliveryMethod.isActive());
        assertEquals(price, deliveryMethod.getPrice());
        assertTrue(deliveryMethod.isFreeForOrderAbove());
        assertEquals(deliveries, deliveryMethod.getDeliveries());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        DeliveryMethod deliveryMethod1 = new DeliveryMethod();
        deliveryMethod1.setId(1L);

        DeliveryMethod deliveryMethod2 = new DeliveryMethod();
        deliveryMethod2.setId(1L);

        DeliveryMethod deliveryMethod3 = new DeliveryMethod();
        deliveryMethod3.setId(2L);

        // Assert
        assertEquals(deliveryMethod1, deliveryMethod2);
        assertNotEquals(deliveryMethod1, deliveryMethod3);
        assertEquals(deliveryMethod1.hashCode(), deliveryMethod2.hashCode());
        assertNotEquals(deliveryMethod1.hashCode(), deliveryMethod3.hashCode());
    }

    @Test
    void testAnnotations() {
        // Verify JPA annotations
        assertNotNull(DeliveryMethod.class.getAnnotation(Entity.class));
        assertNotNull(DeliveryMethod.class.getAnnotation(Table.class));

        try {
            // Check ID field
            var idField = DeliveryMethod.class.getDeclaredField("id");
            assertNotNull(idField.getAnnotation(Id.class));
            assertNotNull(idField.getAnnotation(GeneratedValue.class));

            // Check name field
            var nameField = DeliveryMethod.class.getDeclaredField("name");
            assertNotNull(nameField.getAnnotation(Column.class));

            // Check courierType field
            var courierTypeField = DeliveryMethod.class.getDeclaredField("courierType");
            assertNotNull(courierTypeField.getAnnotation(Enumerated.class));
            assertNotNull(courierTypeField.getAnnotation(Column.class));

            // Check deliveries field
            var deliveriesField = DeliveryMethod.class.getDeclaredField("deliveries");
            assertNotNull(deliveriesField.getAnnotation(OneToMany.class));
        } catch (NoSuchFieldException e) {
            fail("Expected field not found", e);
        }
    }

}