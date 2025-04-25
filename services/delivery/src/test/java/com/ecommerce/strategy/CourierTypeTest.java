package com.ecommerce.strategy;

import com.ecommerce.deliverymethod.DeliveryMethod;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CourierTypeTest {

    @Test
    void testGetAll() {
        // Act
        List<Map<String, String>> courierTypes = CourierType.getAll();

        // Assert
        assertEquals(3, courierTypes.size());

        assertTrue(courierTypes.stream()
                .anyMatch(type ->
                        type.get("type").equals("PACKETA") &&
                                type.get("name").equals("Zásilkovna")
                )
        );

        assertTrue(courierTypes.stream()
                .anyMatch(type ->
                        type.get("type").equals("BALIKOVNA") &&
                                type.get("name").equals("Balíkovna")
                )
        );

        assertTrue(courierTypes.stream()
                .anyMatch(type ->
                        type.get("type").equals("OTHER") &&
                                type.get("name").equals("Ostatní")
                )
        );
    }

    @Test
    void testGetType() {
        // Arrange
        DeliveryMethod deliveryMethod = Mockito.mock(DeliveryMethod.class);
        when(deliveryMethod.getCourierType()).thenReturn(CourierType.PACKETA);

        // Act
        Map<String, String> type = CourierType.getType(deliveryMethod);

        // Assert
        assertNotNull(type);
        assertEquals("PACKETA", type.get("type"));
        assertEquals("Zásilkovna", type.get("name"));
    }

    @Test
    void testEnumValues() {
        // Verify enum values
        CourierType[] types = CourierType.values();
        assertEquals(3, types.length);

        CourierType packeta = CourierType.PACKETA;
        assertEquals("Zásilkovna", packeta.getName());

        CourierType balikovna = CourierType.BALIKOVNA;
        assertEquals("Balíkovna", balikovna.getName());

        CourierType other = CourierType.OTHER;
        assertEquals("Ostatní", other.getName());
    }
}