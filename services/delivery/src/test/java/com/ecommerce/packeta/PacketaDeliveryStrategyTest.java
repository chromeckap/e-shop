package com.ecommerce.packeta;

import com.ecommerce.delivery.Delivery;
import com.ecommerce.delivery.DeliveryStatus;
import com.ecommerce.strategy.CourierHandler;
import com.ecommerce.strategy.CourierType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PacketaDeliveryStrategyTest {

    @Test
    void testProcessDelivery_ValidDelivery() {
        // Arrange
        PacketaDeliveryStrategy strategy = new PacketaDeliveryStrategy();
        Delivery delivery = new Delivery();

        // Act
        strategy.processDelivery(delivery);

        // Assert
        assertEquals(DeliveryStatus.CREATED, delivery.getStatus());
    }

    @Test
    void testProcessDelivery_NullDelivery() {
        // Arrange
        PacketaDeliveryStrategy strategy = new PacketaDeliveryStrategy();

        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> strategy.processDelivery(null),
                "Doručení nesmí být prázdné."
        );
    }

    @Test
    void testGetWidgetUrl() {
        // Arrange
        PacketaDeliveryStrategy strategy = new PacketaDeliveryStrategy();

        // Act
        String widgetUrl = strategy.getWidgetUrl();

        // Assert
        assertEquals("https://widget.packeta.com/v6/", widgetUrl);
    }

    @Test
    void testCourierHandlerAnnotation() {
        // Arrange
        CourierHandler courierHandler = PacketaDeliveryStrategy.class.getAnnotation(CourierHandler.class);

        // Assert
        assertNotNull(courierHandler);
        assertEquals(CourierType.PACKETA, courierHandler.value());
    }
}