package com.ecommerce.payment;

import com.ecommerce.feignclient.user.UserResponse;
import com.ecommerce.exception.PaymentNotFoundException;
import com.stripe.exception.StripeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private PaymentRequest paymentRequest;
    private PaymentResponse paymentResponse;

    @BeforeEach
    void setUp() {
        // Create test user
        UserResponse user = new UserResponse(
                1L, "First name", "Second name", "example@email.com"
        );

        // Create test payment request
        paymentRequest = new PaymentRequest(
                null,
                100L,
                new BigDecimal("99.99"),
                1L,
                user,
                "2023-01-01T12:00:00"
        );

        // Create test payment response
        paymentResponse = PaymentResponse.builder()
                .id(1L)
                .orderId(100L)
                .totalPrice(new BigDecimal("99.99"))
                .status(PaymentStatus.CASH_ON_DELIVERY.name())
                .build();
    }

    @Test
    void createPayment_ShouldReturnCreatedStatusAndPaymentId() throws StripeException {
        // Arrange
        when(paymentService.createPayment(paymentRequest)).thenReturn(1L);

        // Act
        ResponseEntity<Long> response = paymentController.createPayment(paymentRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody());
        verify(paymentService).createPayment(paymentRequest);
    }

    @Test
    void getPaymentUrlById_ShouldReturnOkStatusAndUrl() throws StripeException {
        // Arrange
        String expectedUrl = "https://stripe.com/session/123";
        when(paymentService.getPaymentUrlById(1L)).thenReturn(expectedUrl);

        // Act
        ResponseEntity<String> response = paymentController.getPaymentUrlById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUrl, response.getBody());
        verify(paymentService).getPaymentUrlById(1L);
    }

    @Test
    void getPaymentByOrderId_ShouldReturnOkStatusAndPaymentResponse() {
        // Arrange
        when(paymentService.getPaymentByOrderId(100L)).thenReturn(paymentResponse);

        // Act
        ResponseEntity<PaymentResponse> response = paymentController.getPaymentByOrderId(100L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(paymentResponse, response.getBody());
        verify(paymentService).getPaymentByOrderId(100L);
    }

    @Test
    void getPaymentByOrderId_WhenPaymentNotFound_ServiceShouldThrowException() {
        // Arrange
        when(paymentService.getPaymentByOrderId(100L)).thenThrow(
                new PaymentNotFoundException("Platba s ID objednávky 100 nebyla nalezena.")
        );

        // Act & Assert
        try {
            paymentController.getPaymentByOrderId(100L);
        } catch (PaymentNotFoundException e) {
            assertEquals("Platba s ID objednávky 100 nebyla nalezena.", e.getMessage());
        }

        verify(paymentService).getPaymentByOrderId(100L);
    }
}