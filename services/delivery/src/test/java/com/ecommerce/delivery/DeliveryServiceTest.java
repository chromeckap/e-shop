package com.ecommerce.delivery;

import com.ecommerce.deliverymethod.DeliveryMethod;
import com.ecommerce.deliverymethod.DeliveryMethodService;
import com.ecommerce.strategy.CourierType;
import com.ecommerce.strategy.DeliveryStrategy;
import com.ecommerce.strategy.DeliveryStrategyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private DeliveryMapper deliveryMapper;

    @Mock
    private DeliveryMethodService deliveryMethodService;

    @Mock
    private DeliveryStrategyFactory deliveryStrategyFactory;

    @InjectMocks
    private DeliveryService deliveryService;

    @Test
    void testCreateDelivery_Success() {
        // Arrange
        Long orderId = 1L;
        Long deliveryMethodId = 2L;
        DeliveryRequest validRequest = new DeliveryRequest(orderId, deliveryMethodId);

        DeliveryMethod mockDeliveryMethod = mock(DeliveryMethod.class);
        when(mockDeliveryMethod.getCourierType()).thenReturn(CourierType.PACKETA);

        Delivery mockDelivery = mock(Delivery.class);
        when(mockDelivery.getId()).thenReturn(3L);

        when(deliveryMethodService.findDeliveryMethodById(deliveryMethodId))
                .thenReturn(mockDeliveryMethod);

        when(deliveryMapper.toDelivery(validRequest)).thenReturn(mockDelivery);

        DeliveryStrategy mockStrategy = mock(DeliveryStrategy.class);
        when(deliveryStrategyFactory.getStrategy(CourierType.PACKETA))
                .thenReturn(mockStrategy);

        when(deliveryRepository.save(mockDelivery)).thenReturn(mockDelivery);

        // Act
        Long deliveryId = deliveryService.createDelivery(validRequest);

        // Assert
        assertNotNull(deliveryId);
        assertEquals(3L, deliveryId);

        // Verify interactions
        verify(deliveryMethodService).findDeliveryMethodById(deliveryMethodId);
        verify(deliveryMapper).toDelivery(validRequest);
        verify(mockDelivery).setMethod(mockDeliveryMethod);
        verify(deliveryStrategyFactory).getStrategy(CourierType.PACKETA);
        verify(mockStrategy).processDelivery(mockDelivery);
        verify(deliveryRepository).save(mockDelivery);
    }

    @Test
    void testCreateDelivery_NullRequest() {
        // Act & Assert
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            deliveryService.createDelivery(null);
        });

        assertEquals("Požadavek na doručení nesmí být prázdný", exception.getMessage());
    }

    @Test
    void testCreateDelivery_ServiceAnnotations() {
        // Verify class annotations
        Annotation serviceAnnotation = DeliveryService.class.getAnnotation(Service.class);
        assertNotNull(serviceAnnotation, "Service annotation should be present");

        // Verify method annotations
        try {
            Method createDeliveryMethod = DeliveryService.class.getMethod("createDelivery", DeliveryRequest.class);
            Transactional transactionalAnnotation = createDeliveryMethod.getAnnotation(Transactional.class);
            assertNotNull(transactionalAnnotation, "Transactional annotation should be present on createDelivery method");
        } catch (NoSuchMethodException e) {
            fail("Method not found", e);
        }
    }

    @Test
    void testCreateDelivery_MethodInteractions() {
        // Arrange
        Long orderId = 1L;
        Long deliveryMethodId = 2L;
        DeliveryRequest validRequest = new DeliveryRequest(orderId, deliveryMethodId);

        DeliveryMethod mockDeliveryMethod = mock(DeliveryMethod.class);
        when(mockDeliveryMethod.getCourierType()).thenReturn(CourierType.PACKETA);

        Delivery mockDelivery = mock(Delivery.class);
        when(mockDelivery.getId()).thenReturn(3L);

        when(deliveryMethodService.findDeliveryMethodById(deliveryMethodId))
                .thenReturn(mockDeliveryMethod);

        when(deliveryMapper.toDelivery(validRequest)).thenReturn(mockDelivery);

        DeliveryStrategy mockStrategy = mock(DeliveryStrategy.class);
        when(deliveryStrategyFactory.getStrategy(CourierType.PACKETA))
                .thenReturn(mockStrategy);

        when(deliveryRepository.save(mockDelivery)).thenReturn(mockDelivery);

        // Act
        deliveryService.createDelivery(validRequest);

        // Assert method call order and interactions
        var inOrder = inOrder(
                deliveryMethodService,
                deliveryMapper,
                mockDelivery,
                deliveryStrategyFactory,
                mockStrategy,
                deliveryRepository
        );

        inOrder.verify(deliveryMethodService).findDeliveryMethodById(deliveryMethodId);
        inOrder.verify(deliveryMapper).toDelivery(validRequest);
        inOrder.verify(mockDelivery).setMethod(mockDeliveryMethod);
        inOrder.verify(deliveryStrategyFactory).getStrategy(CourierType.PACKETA);
        inOrder.verify(mockStrategy).processDelivery(mockDelivery);
        inOrder.verify(deliveryRepository).save(mockDelivery);
    }
}