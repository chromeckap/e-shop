package com.ecommerce.paymentmethod;

import com.ecommerce.exception.PaymentMethodNotFoundException;
import com.ecommerce.strategy.PaymentGatewayType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentMethodServiceTest {

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @Mock
    private PaymentMethodMapper paymentMethodMapper;

    @Mock
    private PaymentMethodValidator paymentMethodValidator;

    @InjectMocks
    private PaymentMethodService paymentMethodService;

    @Captor
    private ArgumentCaptor<PaymentMethod> paymentMethodCaptor;

    private PaymentMethod paymentMethod;
    private PaymentMethodRequest paymentMethodRequest;
    private PaymentMethodResponse paymentMethodResponse;
    private List<PaymentMethod> paymentMethods;
    private List<PaymentMethodResponse> paymentMethodResponses;

    @BeforeEach
    void setUp() {
        // Create test payment method
        paymentMethod = PaymentMethod.builder()
                .id(1L)
                .name("Credit Card")
                .gatewayType(PaymentGatewayType.STRIPE_CARD)
                .isActive(true)
                .price(new BigDecimal("5.99"))
                .isFreeForOrderAbove(true)
                .freeForOrderAbove(new BigDecimal("100.00"))
                .build();

        // Create test payment method request
        paymentMethodRequest = new PaymentMethodRequest(
                null,
                "Credit Card",
                PaymentGatewayType.STRIPE_CARD,
                true,
                new BigDecimal("5.99"),
                true,
                new BigDecimal("100.00")
        );

        // Create gateway type map for response
        Map<String, String> gatewayTypeMap = new HashMap<>();
        gatewayTypeMap.put("name", "STRIPE_CARD");
        gatewayTypeMap.put("description", "Stripe Card");

        // Create test payment method response
        paymentMethodResponse = PaymentMethodResponse.builder()
                .id(1L)
                .name("Credit Card")
                .gatewayType(gatewayTypeMap)
                .isActive(true)
                .price(new BigDecimal("5.99"))
                .isFreeForOrderAbove(true)
                .freeForOrderAbove(new BigDecimal("100.00"))
                .build();

        // Create list of payment methods
        paymentMethods = List.of(paymentMethod);

        // Create list of payment method responses
        paymentMethodResponses = List.of(paymentMethodResponse);
    }

    @Test
    void findPaymentMethodById_WhenPaymentMethodExists_ShouldReturnPaymentMethod() {
        // Arrange
        when(paymentMethodRepository.findById(1L)).thenReturn(Optional.of(paymentMethod));
        doNothing().when(paymentMethodValidator).validatePaymentMethodAccessible(paymentMethod);

        // Act
        PaymentMethod result = paymentMethodService.findPaymentMethodById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(paymentMethodRepository).findById(1L);
        verify(paymentMethodValidator).validatePaymentMethodAccessible(paymentMethod);
    }

    @Test
    void findPaymentMethodById_WhenPaymentMethodDoesNotExist_ShouldThrowException() {
        // Arrange
        when(paymentMethodRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        PaymentMethodNotFoundException exception = assertThrows(
                PaymentMethodNotFoundException.class,
                () -> paymentMethodService.findPaymentMethodById(1L)
        );
        assertTrue(exception.getMessage().contains("nebyla nalezena"));
        verify(paymentMethodRepository).findById(1L);
        verify(paymentMethodValidator, never()).validatePaymentMethodAccessible(any());
    }

    @Test
    void findPaymentMethodById_WhenIdIsNull_ShouldThrowException() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> paymentMethodService.findPaymentMethodById(null)
        );
        verify(paymentMethodRepository, never()).findById(anyLong());
        verify(paymentMethodValidator, never()).validatePaymentMethodAccessible(any());
    }

    @Test
    void getPaymentMethodById_WhenPaymentMethodExists_ShouldReturnPaymentMethodResponse() {
        // Arrange
        when(paymentMethodRepository.findById(1L)).thenReturn(Optional.of(paymentMethod));
        doNothing().when(paymentMethodValidator).validatePaymentMethodAccessible(paymentMethod);
        when(paymentMethodMapper.toResponse(paymentMethod)).thenReturn(paymentMethodResponse);

        // Act
        PaymentMethodResponse result = paymentMethodService.getPaymentMethodById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(paymentMethodRepository).findById(1L);
        verify(paymentMethodValidator).validatePaymentMethodAccessible(paymentMethod);
        verify(paymentMethodMapper).toResponse(paymentMethod);
    }

    @Test
    void getAllPaymentMethods_ShouldReturnAllPaymentMethods() {
        // Arrange
        when(paymentMethodRepository.findAll()).thenReturn(paymentMethods);
        when(paymentMethodMapper.toResponse(paymentMethod)).thenReturn(paymentMethodResponse);

        // Act
        List<PaymentMethodResponse> result = paymentMethodService.getAllPaymentMethods();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(paymentMethodResponse, result.get(0));
        verify(paymentMethodRepository).findAll();
        verify(paymentMethodMapper).toResponse(paymentMethod);
    }

    @Test
    void getActivePaymentMethods_ShouldReturnActivePaymentMethods() {
        // Arrange
        when(paymentMethodRepository.findAllByIsActive(true)).thenReturn(paymentMethods);
        when(paymentMethodMapper.toResponse(paymentMethod)).thenReturn(paymentMethodResponse);

        // Act
        List<PaymentMethodResponse> result = paymentMethodService.getActivePaymentMethods();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(paymentMethodResponse, result.get(0));
        verify(paymentMethodRepository).findAllByIsActive(true);
        verify(paymentMethodMapper).toResponse(paymentMethod);
    }

    @Test
    void getPaymentGatewayTypes_ShouldReturnAllTypes() {
        // Arrange
        List<Map<String, String>> expectedTypes = new ArrayList<>();
        Map<String, String> stripeType = new HashMap<>();
        stripeType.put("name", "STRIPE_CARD");
        stripeType.put("description", "Stripe Card");
        expectedTypes.add(stripeType);

        Map<String, String> codType = new HashMap<>();
        codType.put("name", "CASH_ON_DELIVERY");
        codType.put("description", "Cash on Delivery");
        expectedTypes.add(codType);

        // Mock static call to PaymentGatewayType.getAll()
        // Note: In a real test, you would use MockedStatic for this
        // This is simplified for demonstration
        try (MockedStatic<PaymentGatewayType> mockedStatic = mockStatic(PaymentGatewayType.class)) {
            mockedStatic.when(PaymentGatewayType::getAll).thenReturn(expectedTypes);

            // Act
            List<Map<String, String>> result = paymentMethodService.getPaymentGatewayTypes();

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("STRIPE_CARD", result.get(0).get("name"));
            assertEquals("CASH_ON_DELIVERY", result.get(1).get("name"));
        }
    }

    @Test
    void createPaymentMethod_ShouldCreateAndReturnId() {
        // Arrange
        PaymentMethod newPaymentMethod = PaymentMethod.builder()
                .name("Credit Card")
                .gatewayType(PaymentGatewayType.STRIPE_CARD)
                .isActive(true)
                .price(new BigDecimal("5.99"))
                .isFreeForOrderAbove(true)
                .freeForOrderAbove(new BigDecimal("100.00"))
                .build();

        PaymentMethod savedPaymentMethod = PaymentMethod.builder()
                .id(1L)
                .name("Credit Card")
                .gatewayType(PaymentGatewayType.STRIPE_CARD)
                .isActive(true)
                .price(new BigDecimal("5.99"))
                .isFreeForOrderAbove(true)
                .freeForOrderAbove(new BigDecimal("100.00"))
                .build();

        when(paymentMethodMapper.toPaymentMethod(paymentMethodRequest)).thenReturn(newPaymentMethod);
        when(paymentMethodRepository.save(newPaymentMethod)).thenReturn(savedPaymentMethod);

        // Act
        Long result = paymentMethodService.createPaymentMethod(paymentMethodRequest);

        // Assert
        assertEquals(1L, result);
        verify(paymentMethodMapper).toPaymentMethod(paymentMethodRequest);
        verify(paymentMethodRepository).save(newPaymentMethod);
    }

    @Test
    void createPaymentMethod_WhenRequestIsNull_ShouldThrowException() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> paymentMethodService.createPaymentMethod(null)
        );
        verify(paymentMethodMapper, never()).toPaymentMethod(any());
        verify(paymentMethodRepository, never()).save(any());
    }

    @Test
    void updatePaymentMethod_ShouldUpdateAndReturnId() {
        // Arrange
        PaymentMethod existingPaymentMethod = PaymentMethod.builder()
                .id(1L)
                .name("Old Name")
                .gatewayType(PaymentGatewayType.STRIPE_CARD)
                .isActive(true)
                .price(new BigDecimal("5.99"))
                .build();

        PaymentMethod updatedPaymentMethod = PaymentMethod.builder()
                .name("New Name")
                .gatewayType(PaymentGatewayType.STRIPE_CARD)
                .isActive(true)
                .price(new BigDecimal("6.99"))
                .build();

        PaymentMethod savedPaymentMethod = PaymentMethod.builder()
                .id(1L)
                .name("New Name")
                .gatewayType(PaymentGatewayType.STRIPE_CARD)
                .isActive(true)
                .price(new BigDecimal("6.99"))
                .build();

        PaymentMethodRequest updateRequest = new PaymentMethodRequest(
                null,
                "New Name",
                PaymentGatewayType.STRIPE_CARD,
                true,
                new BigDecimal("6.99"),
                false,
                null
        );

        when(paymentMethodRepository.findById(1L)).thenReturn(Optional.of(existingPaymentMethod));
        doNothing().when(paymentMethodValidator).validatePaymentMethodAccessible(existingPaymentMethod);
        when(paymentMethodMapper.toPaymentMethod(updateRequest)).thenReturn(updatedPaymentMethod);
        when(paymentMethodRepository.save(any(PaymentMethod.class))).thenReturn(savedPaymentMethod);

        // Act
        Long result = paymentMethodService.updatePaymentMethod(1L, updateRequest);

        // Assert
        assertEquals(1L, result);
        verify(paymentMethodRepository).findById(1L);
        verify(paymentMethodValidator).validatePaymentMethodAccessible(existingPaymentMethod);
        verify(paymentMethodMapper).toPaymentMethod(updateRequest);

        // Capture the payment method being saved to verify ID is set correctly
        verify(paymentMethodRepository).save(paymentMethodCaptor.capture());
        PaymentMethod capturedPaymentMethod = paymentMethodCaptor.getValue();
        assertEquals(1L, capturedPaymentMethod.getId());
        assertEquals("New Name", capturedPaymentMethod.getName());
    }

    @Test
    void updatePaymentMethod_WhenIdIsNull_ShouldThrowException() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> paymentMethodService.updatePaymentMethod(null, paymentMethodRequest)
        );
        verify(paymentMethodRepository, never()).findById(anyLong());
        verify(paymentMethodMapper, never()).toPaymentMethod(any());
        verify(paymentMethodRepository, never()).save(any());
    }

    @Test
    void updatePaymentMethod_WhenRequestIsNull_ShouldThrowException() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> paymentMethodService.updatePaymentMethod(1L, null)
        );
        verify(paymentMethodRepository, never()).findById(anyLong());
        verify(paymentMethodMapper, never()).toPaymentMethod(any());
        verify(paymentMethodRepository, never()).save(any());
    }

    @Test
    void deletePaymentMethodById_ShouldDeletePaymentMethod() {
        // Arrange
        when(paymentMethodRepository.findById(1L)).thenReturn(Optional.of(paymentMethod));
        doNothing().when(paymentMethodValidator).validatePaymentMethodAccessible(paymentMethod);
        doNothing().when(paymentMethodRepository).delete(paymentMethod);

        // Act
        paymentMethodService.deletePaymentMethodById(1L);

        // Assert
        verify(paymentMethodRepository).findById(1L);
        verify(paymentMethodValidator).validatePaymentMethodAccessible(paymentMethod);
        verify(paymentMethodRepository).delete(paymentMethod);
    }

    @Test
    void deletePaymentMethodById_WhenIdIsNull_ShouldThrowException() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> paymentMethodService.deletePaymentMethodById(null)
        );
        verify(paymentMethodRepository, never()).findById(anyLong());
        verify(paymentMethodValidator, never()).validatePaymentMethodAccessible(any());
        verify(paymentMethodRepository, never()).delete(any());
    }
}