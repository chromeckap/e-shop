package com.ecommerce.deliverymethod;

import com.ecommerce.strategy.CourierType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeliveryMethodMapperTest {

    private DeliveryMethodMapper deliveryMethodMapper;

    @BeforeEach
    void setUp() {
        deliveryMethodMapper = new DeliveryMethodMapper();
    }

    @Test
    void testToDeliveryMethod_FullRequest() {
        // Arrange
        DeliveryMethodRequest request = new DeliveryMethodRequest(
                1L,
                "Test Delivery Method",
                CourierType.PACKETA,
                true,
                BigDecimal.valueOf(5.99),
                true,
                BigDecimal.valueOf(50.00)
        );

        // Act
        DeliveryMethod deliveryMethod = deliveryMethodMapper.toDeliveryMethod(request);

        // Assert
        assertNotNull(deliveryMethod);
        assertEquals(request.id(), deliveryMethod.getId());
        assertEquals(request.name(), deliveryMethod.getName());
        assertEquals(request.type(), deliveryMethod.getCourierType());
        assertEquals(request.isActive(), deliveryMethod.isActive());
        assertEquals(request.price(), deliveryMethod.getPrice());
        assertEquals(request.isFreeForOrderAbove(), deliveryMethod.isFreeForOrderAbove());
        assertEquals(request.freeForOrderAbove(), deliveryMethod.getFreeForOrderAbove());
    }

    @Test
    void testToDeliveryMethod_NullRequest() {
        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                deliveryMethodMapper.toDeliveryMethod(null)
        );
    }

    @Test
    void testToResponse_FullDeliveryMethod() {
        // Arrange
        DeliveryMethod deliveryMethod = mock(DeliveryMethod.class);
        when(deliveryMethod.getId()).thenReturn(1L);
        when(deliveryMethod.getName()).thenReturn("Test Delivery Method");
        when(deliveryMethod.getCourierType()).thenReturn(CourierType.PACKETA);
        when(deliveryMethod.isActive()).thenReturn(true);
        when(deliveryMethod.getPrice()).thenReturn(BigDecimal.valueOf(5.99));
        when(deliveryMethod.isFreeForOrderAbove()).thenReturn(true);
        when(deliveryMethod.getFreeForOrderAbove()).thenReturn(BigDecimal.valueOf(50.00));

        // Act
        DeliveryMethodResponse response = deliveryMethodMapper.toResponse(deliveryMethod);

        // Assert
        assertNotNull(response);
        assertEquals(deliveryMethod.getId(), response.id());
        assertEquals(deliveryMethod.getName(), response.name());
        assertEquals(deliveryMethod.isActive(), response.isActive());
        assertEquals(deliveryMethod.getPrice(), response.price());
        assertEquals(deliveryMethod.isFreeForOrderAbove(), response.isFreeForOrderAbove());
        assertEquals(deliveryMethod.getFreeForOrderAbove(), response.freeForOrderAbove());
    }

    @Test
    void testToResponse_NullDeliveryMethod() {
        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                deliveryMethodMapper.toResponse(null)
        );
    }

    @Test
    void testComponentAnnotation() {
        // Verify that the class is annotated with @Component
        Component componentAnnotation = DeliveryMethodMapper.class.getAnnotation(Component.class);
        assertNotNull(componentAnnotation);
    }

    @Test
    void testMapperConsistency() {
        // Arrange
        DeliveryMethodRequest request = new DeliveryMethodRequest(
                1L,
                "Test Delivery Method",
                CourierType.PACKETA,
                true,
                BigDecimal.valueOf(5.99),
                true,
                BigDecimal.valueOf(50.00)
        );

        // Act
        DeliveryMethod deliveryMethod = deliveryMethodMapper.toDeliveryMethod(request);
        DeliveryMethodResponse response = deliveryMethodMapper.toResponse(deliveryMethod);

        // Assert
        assertEquals(request.id(), deliveryMethod.getId());
        assertEquals(request.name(), deliveryMethod.getName());
        assertEquals(request.type(), deliveryMethod.getCourierType());
        assertEquals(request.isActive(), deliveryMethod.isActive());
        assertEquals(request.price(), deliveryMethod.getPrice());
        assertEquals(request.isFreeForOrderAbove(), deliveryMethod.isFreeForOrderAbove());
        assertEquals(request.freeForOrderAbove(), deliveryMethod.getFreeForOrderAbove());
    }
}