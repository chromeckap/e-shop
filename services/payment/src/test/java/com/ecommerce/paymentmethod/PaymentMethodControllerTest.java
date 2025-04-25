package com.ecommerce.paymentmethod;

import com.ecommerce.exception.PaymentMethodNotFoundException;
import com.ecommerce.strategy.PaymentGatewayType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentMethodControllerTest {

    @Mock
    private PaymentMethodService paymentMethodService;

    @InjectMocks
    private PaymentMethodController paymentMethodController;

    private PaymentMethodRequest paymentMethodRequest;
    private PaymentMethodResponse paymentMethodResponse;
    private List<PaymentMethodResponse> paymentMethodResponses;
    private List<Map<String, String>> paymentGatewayTypes;

    @BeforeEach
    void setUp() {
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

        // Create list of payment method responses
        paymentMethodResponses = List.of(paymentMethodResponse);

        // Create payment gateway types list
        paymentGatewayTypes = new ArrayList<>();
        Map<String, String> stripeType = new HashMap<>();
        stripeType.put("name", "STRIPE_CARD");
        stripeType.put("description", "Stripe Card");
        paymentGatewayTypes.add(stripeType);

        Map<String, String> codType = new HashMap<>();
        codType.put("name", "CASH_ON_DELIVERY");
        codType.put("description", "Cash on Delivery");
        paymentGatewayTypes.add(codType);
    }

    @Test
    void getPaymentMethodById_ShouldReturnOkWithPaymentMethod() {
        // Arrange
        when(paymentMethodService.getPaymentMethodById(1L)).thenReturn(paymentMethodResponse);

        // Act
        ResponseEntity<PaymentMethodResponse> response = paymentMethodController.getPaymentMethodById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(paymentMethodResponse, response.getBody());
        verify(paymentMethodService).getPaymentMethodById(1L);
    }

    @Test
    void getAllPaymentMethods_ShouldReturnOkWithAllPaymentMethods() {
        // Arrange
        when(paymentMethodService.getAllPaymentMethods()).thenReturn(paymentMethodResponses);

        // Act
        ResponseEntity<List<PaymentMethodResponse>> response = paymentMethodController.getAllPaymentMethods();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(paymentMethodResponses, response.getBody());
        verify(paymentMethodService).getAllPaymentMethods();
    }

    @Test
    void getActivePaymentMethods_ShouldReturnOkWithActivePaymentMethods() {
        // Arrange
        when(paymentMethodService.getActivePaymentMethods()).thenReturn(paymentMethodResponses);

        // Act
        ResponseEntity<List<PaymentMethodResponse>> response = paymentMethodController.getActivePaymentMethods();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(paymentMethodResponses, response.getBody());
        verify(paymentMethodService).getActivePaymentMethods();
    }

    @Test
    void getPaymentGatewayTypes_ShouldReturnOkWithAllTypes() {
        // Arrange
        when(paymentMethodService.getPaymentGatewayTypes()).thenReturn(paymentGatewayTypes);

        // Act
        ResponseEntity<List<Map<String, String>>> response = paymentMethodController.getPaymentGatewayTypes();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(paymentGatewayTypes, response.getBody());
        verify(paymentMethodService).getPaymentGatewayTypes();
    }

    @Test
    void createPaymentMethod_ShouldReturnCreatedWithId() {
        // Arrange
        when(paymentMethodService.createPaymentMethod(paymentMethodRequest)).thenReturn(1L);

        // Act
        ResponseEntity<Long> response = paymentMethodController.createPaymentMethod(paymentMethodRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody());
        verify(paymentMethodService).createPaymentMethod(paymentMethodRequest);
    }

    @Test
    void updatePaymentMethod_ShouldReturnOkWithId() {
        // Arrange
        when(paymentMethodService.updatePaymentMethod(1L, paymentMethodRequest)).thenReturn(1L);

        // Act
        ResponseEntity<Long> response = paymentMethodController.updatePaymentMethod(1L, paymentMethodRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody());
        verify(paymentMethodService).updatePaymentMethod(1L, paymentMethodRequest);
    }

    @Test
    void deletePaymentMethodById_ShouldReturnNoContent() {
        // Arrange
        doNothing().when(paymentMethodService).deletePaymentMethodById(1L);

        // Act
        ResponseEntity<Void> response = paymentMethodController.deletePaymentMethodById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(paymentMethodService).deletePaymentMethodById(1L);
    }

    @Test
    void getPaymentMethodById_WhenPaymentMethodNotFound_ShouldPropagateException() {
        // Arrange
        when(paymentMethodService.getPaymentMethodById(1L)).thenThrow(
                new PaymentMethodNotFoundException("Metoda pro platbu s ID 1 nebyla nalezena.")
        );

        // Act & Assert
        try {
            paymentMethodController.getPaymentMethodById(1L);
        } catch (PaymentMethodNotFoundException e) {
            assertEquals("Metoda pro platbu s ID 1 nebyla nalezena.", e.getMessage());
        }

        verify(paymentMethodService).getPaymentMethodById(1L);
    }
}