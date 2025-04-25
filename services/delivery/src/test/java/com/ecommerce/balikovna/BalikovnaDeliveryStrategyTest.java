package com.ecommerce.balikovna;

import com.ecommerce.delivery.Delivery;
import com.ecommerce.delivery.DeliveryStatus;
import com.ecommerce.strategy.CourierHandler;
import com.ecommerce.strategy.CourierType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BalikovnaDeliveryStrategyTest {

    @Test
    void testProcessDelivery_ValidDelivery() {
        // Arrange
        BalikovnaDeliveryStrategy strategy = new BalikovnaDeliveryStrategy();
        Delivery delivery = new Delivery();

        // Act
        strategy.processDelivery(delivery);

        // Assert
        assertEquals(DeliveryStatus.CREATED, delivery.getStatus());
    }

    @Test
    void testProcessDelivery_NullDelivery() {
        // Arrange
        BalikovnaDeliveryStrategy strategy = new BalikovnaDeliveryStrategy();

        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> strategy.processDelivery(null),
                "Doručení nesmí být prázdné."
        );
    }

    @Test
    void testGetWidgetUrl() {
        // Arrange
        BalikovnaDeliveryStrategy strategy = new BalikovnaDeliveryStrategy();

        // Act
        String widgetUrl = strategy.getWidgetUrl();

        // Assert
        assertEquals("https://b2c.cpost.cz/locations/?type=BALIKOVNY", widgetUrl);
    }

    @Test
    void testCourierHandlerAnnotation() {
        // Arrange
        CourierHandler courierHandler = BalikovnaDeliveryStrategy.class.getAnnotation(CourierHandler.class);

        // Assert
        assertNotNull(courierHandler);
        assertEquals(CourierType.BALIKOVNA, courierHandler.value());
    }
}