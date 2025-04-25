package com.ecommerce.other;

import com.ecommerce.delivery.Delivery;
import com.ecommerce.delivery.DeliveryStatus;
import com.ecommerce.strategy.CourierHandler;
import com.ecommerce.strategy.CourierType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OtherDeliveryStrategyTest {

    @Test
    void testProcessDelivery_ValidDelivery() {
        // Arrange
        OtherDeliveryStrategy strategy = new OtherDeliveryStrategy();
        Delivery delivery = new Delivery();

        // Act
        strategy.processDelivery(delivery);

        // Assert
        assertEquals(DeliveryStatus.CREATED, delivery.getStatus());
    }

    @Test
    void testProcessDelivery_NullDelivery() {
        // Arrange
        OtherDeliveryStrategy strategy = new OtherDeliveryStrategy();

        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> strategy.processDelivery(null),
                "Doručení nesmí být prázdné."
        );
    }

    @Test
    void testGetWidgetUrl() {
        // Arrange
        OtherDeliveryStrategy strategy = new OtherDeliveryStrategy();

        // Act
        String widgetUrl = strategy.getWidgetUrl();

        // Assert
        assertNull(widgetUrl);
    }

    @Test
    void testCourierHandlerAnnotation() {
        // Arrange
        CourierHandler courierHandler = OtherDeliveryStrategy.class.getAnnotation(CourierHandler.class);

        // Assert
        assertNotNull(courierHandler);
        assertEquals(CourierType.OTHER, courierHandler.value());
    }
}