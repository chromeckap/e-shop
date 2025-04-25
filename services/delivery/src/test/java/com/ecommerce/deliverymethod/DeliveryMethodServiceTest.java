package com.ecommerce.deliverymethod;

import com.ecommerce.exception.DeliveryMethodNotFoundException;
import com.ecommerce.strategy.CourierType;
import com.ecommerce.strategy.DeliveryStrategy;
import com.ecommerce.strategy.DeliveryStrategyFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryMethodServiceTest {

    @Mock
    private DeliveryMethodRepository deliveryMethodRepository;

    @Mock
    private DeliveryMethodMapper deliveryMethodMapper;

    @Mock
    private DeliveryMethodValidator deliveryMethodValidator;

    @Mock
    private DeliveryStrategyFactory deliveryStrategyFactory;

    @InjectMocks
    private DeliveryMethodService deliveryMethodService;

    private DeliveryMethodRequest createMockRequest() {
        return new DeliveryMethodRequest(
                1L,
                "Test Delivery Method",
                CourierType.PACKETA,
                true,
                BigDecimal.valueOf(5.99),
                true,
                BigDecimal.valueOf(50.00)
        );
    }

    @Test
    void testFindDeliveryMethodById_Successful() {
        // Arrange
        Long id = 1L;
        DeliveryMethod mockDeliveryMethod = mock(DeliveryMethod.class);
        when(deliveryMethodRepository.findById(id)).thenReturn(Optional.of(mockDeliveryMethod));
        doNothing().when(deliveryMethodValidator).validatedDeliveryMethodAccessible(mockDeliveryMethod);

        // Act
        DeliveryMethod result = deliveryMethodService.findDeliveryMethodById(id);

        // Assert
        assertNotNull(result);
        assertEquals(mockDeliveryMethod, result);
        verify(deliveryMethodRepository).findById(id);
        verify(deliveryMethodValidator).validatedDeliveryMethodAccessible(mockDeliveryMethod);
    }

    @Test
    void testFindDeliveryMethodById_NotFound() {
        // Arrange
        Long id = 1L;
        when(deliveryMethodRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DeliveryMethodNotFoundException.class,
                () -> deliveryMethodService.findDeliveryMethodById(id)
        );
    }

    @Test
    void testFindDeliveryMethodById_NullId() {
        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> deliveryMethodService.findDeliveryMethodById(null)
        );
    }

    @Test
    void testGetDeliveryMethodById_Successful() {
        // Arrange
        Long id = 1L;
        DeliveryMethod mockDeliveryMethod = mock(DeliveryMethod.class);
        DeliveryMethodResponse mockResponse = mock(DeliveryMethodResponse.class);

        when(deliveryMethodRepository.findById(id)).thenReturn(Optional.of(mockDeliveryMethod));
        doNothing().when(deliveryMethodValidator).validatedDeliveryMethodAccessible(mockDeliveryMethod);
        when(deliveryMethodMapper.toResponse(mockDeliveryMethod)).thenReturn(mockResponse);

        // Act
        DeliveryMethodResponse result = deliveryMethodService.getDeliveryMethodById(id);

        // Assert
        assertNotNull(result);
        assertEquals(mockResponse, result);
        verify(deliveryMethodMapper).toResponse(mockDeliveryMethod);
    }

    @Test
    void testGetAllDeliveryMethods() {
        // Arrange
        DeliveryMethod mockDeliveryMethod = mock(DeliveryMethod.class);
        DeliveryMethodResponse mockResponse = mock(DeliveryMethodResponse.class);

        List<DeliveryMethod> mockMethods = List.of(mockDeliveryMethod);
        when(deliveryMethodRepository.findAll()).thenReturn(mockMethods);
        when(deliveryMethodMapper.toResponse(mockDeliveryMethod)).thenReturn(mockResponse);

        // Act
        List<DeliveryMethodResponse> result = deliveryMethodService.getAllDeliveryMethods();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(deliveryMethodRepository).findAll();
    }

    @Test
    void testGetActiveDeliveryMethods() {
        // Arrange
        DeliveryMethod mockDeliveryMethod = mock(DeliveryMethod.class);
        DeliveryMethodResponse mockResponse = mock(DeliveryMethodResponse.class);

        List<DeliveryMethod> mockActiveMethods = List.of(mockDeliveryMethod);
        when(deliveryMethodRepository.findAllByIsActive(true)).thenReturn(mockActiveMethods);
        when(deliveryMethodMapper.toResponse(mockDeliveryMethod)).thenReturn(mockResponse);

        // Act
        List<DeliveryMethodResponse> result = deliveryMethodService.getActiveDeliveryMethods();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(deliveryMethodRepository).findAllByIsActive(true);
    }

    @Test
    void testGetCourierWidgetUrl() {
        // Arrange
        String courierType = "PACKETA";
        DeliveryStrategy mockStrategy = mock(DeliveryStrategy.class);
        String expectedUrl = "https://example.com/widget";

        when(deliveryStrategyFactory.getStrategy(CourierType.PACKETA)).thenReturn(mockStrategy);
        when(mockStrategy.getWidgetUrl()).thenReturn(expectedUrl);

        // Act
        String result = deliveryMethodService.getCourierWidgetUrl(courierType);

        // Assert
        assertEquals(expectedUrl, result);
        verify(deliveryStrategyFactory).getStrategy(CourierType.PACKETA);
    }

    @Test
    void testGetCourierTypes() {
        // Act
        List<Map<String, String>> result = deliveryMethodService.getCourierTypes();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(m ->
                m.containsKey("type") && m.containsKey("name")
        ));
    }

    @Test
    void testCreateDeliveryMethod() {
        // Arrange
        DeliveryMethodRequest mockRequest = createMockRequest();
        DeliveryMethod mockDeliveryMethod = mock(DeliveryMethod.class);

        when(deliveryMethodMapper.toDeliveryMethod(mockRequest)).thenReturn(mockDeliveryMethod);
        when(mockDeliveryMethod.getId()).thenReturn(1L);
        when(deliveryMethodRepository.save(mockDeliveryMethod)).thenReturn(mockDeliveryMethod);

        // Act
        Long result = deliveryMethodService.createDeliveryMethod(mockRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result);
        verify(deliveryMethodMapper).toDeliveryMethod(mockRequest);
        verify(deliveryMethodRepository).save(mockDeliveryMethod);
    }

    @Test
    void testUpdateDeliveryMethod() {
        // Arrange
        Long id = 1L;
        DeliveryMethodRequest mockRequest = createMockRequest();
        DeliveryMethod existingMethod = mock(DeliveryMethod.class);
        DeliveryMethod updatedMethod = mock(DeliveryMethod.class);

        when(deliveryMethodRepository.findById(id)).thenReturn(Optional.of(existingMethod));
        doNothing().when(deliveryMethodValidator).validatedDeliveryMethodAccessible(existingMethod);
        when(deliveryMethodMapper.toDeliveryMethod(mockRequest)).thenReturn(updatedMethod);
        when(updatedMethod.getId()).thenReturn(id);
        when(deliveryMethodRepository.save(updatedMethod)).thenReturn(updatedMethod);

        // Act
        Long result = deliveryMethodService.updateDeliveryMethod(id, mockRequest);

        // Assert
        assertNotNull(result);
        assertEquals(id, result);
        verify(deliveryMethodRepository).save(updatedMethod);
    }

    @Test
    void testDeleteDeliveryMethodById() {
        // Arrange
        Long id = 1L;
        DeliveryMethod mockDeliveryMethod = mock(DeliveryMethod.class);

        when(deliveryMethodRepository.findById(id)).thenReturn(Optional.of(mockDeliveryMethod));
        doNothing().when(deliveryMethodValidator).validatedDeliveryMethodAccessible(mockDeliveryMethod);
        doNothing().when(deliveryMethodRepository).delete(mockDeliveryMethod);

        // Act
        assertDoesNotThrow(() -> deliveryMethodService.deleteDeliveryMethodById(id));

        // Assert
        verify(deliveryMethodRepository).findById(id);
        verify(deliveryMethodRepository).delete(mockDeliveryMethod);
    }

    @Test
    void testMethodAnnotations() {
        // Check method-level @Transactional annotations
        Method[] methods = DeliveryMethodService.class.getDeclaredMethods();

        for (Method method : methods) {
            // Skip private, synthetic, and bridge methods
            if (method.isSynthetic() || method.getName().contains("$")) continue;

            // Check methods that should be transactional
            if (method.getName().matches(
                    "findDeliveryMethodById|getDeliveryMethodById|getAllDeliveryMethods|" +
                            "getActiveDeliveryMethods|getCourierTypes|createDeliveryMethod|" +
                            "updateDeliveryMethod|deleteDeliveryMethodById"
            )) {
                Transactional transactional = method.getAnnotation(Transactional.class);
                assertNotNull(transactional,
                        "Method " + method.getName() + " should have @Transactional annotation"
                );
            }
        }
    }
}