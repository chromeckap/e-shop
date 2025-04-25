package com.ecommerce.payment;

import com.ecommerce.exception.PaymentNotFoundException;
import com.ecommerce.feignclient.user.UserResponse;
import com.ecommerce.kafka.PaymentConfirmation;
import com.ecommerce.kafka.PaymentProducer;
import com.ecommerce.paymentmethod.PaymentMethod;
import com.ecommerce.paymentmethod.PaymentMethodService;
import com.ecommerce.strategy.PaymentGatewayType;
import com.ecommerce.strategy.PaymentStrategy;
import com.ecommerce.strategy.PaymentStrategyFactory;
import com.stripe.exception.StripeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private PaymentProducer paymentProducer;

    @Mock
    private PaymentStrategyFactory paymentStrategyFactory;

    @Mock
    private PaymentMethodService paymentMethodService;

    @Mock
    private PaymentStrategy<Payment> paymentStrategy;

    @InjectMocks
    private PaymentService paymentService;

    @Captor
    private ArgumentCaptor<PaymentConfirmation> confirmationCaptor;

    private Payment payment;
    private PaymentMethod paymentMethod;
    private PaymentRequest paymentRequest;
    private PaymentResponse paymentResponse;
    private UserResponse user;

    @BeforeEach
    void setUp() {
        // Create test user
        user = new UserResponse(
                1L, "First name", "Second name", "example@email.com"
        );

        // Create test payment method
        paymentMethod = new PaymentMethod();
        paymentMethod.setId(1L);
        paymentMethod.setName("Test Payment Method");
        paymentMethod.setGatewayType(PaymentGatewayType.STRIPE_CARD);

        // Create test payment
        payment = new Payment() {};
        payment.setId(1L);
        payment.setOrderId(100L);
        payment.setTotalPrice(new BigDecimal("99.99"));
        payment.setStatus(PaymentStatus.UNPAID);
        payment.setMethod(paymentMethod);
        payment.setCreateTime(LocalDateTime.now());

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
                .status(PaymentStatus.UNPAID.name())
                .build();
    }

    @Test
    void findPaymentEntityById_WhenPaymentExists_ShouldReturnPayment() {
        // Arrange
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        // Act
        Payment result = paymentService.findPaymentEntityById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(paymentRepository).findById(1L);
    }

    @Test
    void findPaymentEntityById_WhenPaymentDoesNotExist_ShouldThrowException() {
        // Arrange
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        PaymentNotFoundException exception = assertThrows(
                PaymentNotFoundException.class,
                () -> paymentService.findPaymentEntityById(1L)
        );
        assertTrue(exception.getMessage().contains("nebyla nalezena"));
        verify(paymentRepository).findById(1L);
    }

    @Test
    void findPaymentEntityById_WhenIdIsNull_ShouldThrowException() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> paymentService.findPaymentEntityById(null)
        );
        verify(paymentRepository, never()).findById(anyLong());
    }

    @Test
    void createPayment_WhenStripePayment_ShouldProcessAndSendConfirmation() throws StripeException {
        // Arrange
        when(paymentMethodService.findPaymentMethodById(1L)).thenReturn(paymentMethod);
        when(paymentMapper.toPayment(paymentRequest, PaymentGatewayType.STRIPE_CARD)).thenReturn(payment);
        when(paymentStrategyFactory.getStrategy(PaymentGatewayType.STRIPE_CARD)).thenReturn(paymentStrategy);
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentStrategy.getPaymentUrlById(1L)).thenReturn("https://stripe.com/session/123");

        // Act
        Long result = paymentService.createPayment(paymentRequest);

        // Assert
        assertEquals(1L, result);
        verify(paymentStrategy).processPayment(payment);
        verify(paymentRepository).save(payment);
        verify(paymentProducer).sendPaymentConfirmation(confirmationCaptor.capture());

        PaymentConfirmation capturedConfirmation = confirmationCaptor.getValue();
        assertEquals(100L, capturedConfirmation.orderId());
        assertEquals(new BigDecimal("99.99"), capturedConfirmation.totalPrice());
        assertEquals("Test Payment Method", capturedConfirmation.paymentMethodName());
        assertEquals(user, capturedConfirmation.user());
        assertEquals("https://stripe.com/session/123", capturedConfirmation.sessionUri());
        assertEquals("2023-01-01T12:00:00", capturedConfirmation.OrderCreateTime());
    }

    @Test
    void createPayment_WhenCODPayment_ShouldProcessWithoutSendingConfirmation() throws StripeException {
        // Arrange
        paymentMethod.setGatewayType(PaymentGatewayType.CASH_ON_DELIVERY);

        when(paymentMethodService.findPaymentMethodById(1L)).thenReturn(paymentMethod);
        when(paymentMapper.toPayment(paymentRequest, PaymentGatewayType.CASH_ON_DELIVERY)).thenReturn(payment);
        when(paymentStrategyFactory.getStrategy(PaymentGatewayType.CASH_ON_DELIVERY)).thenReturn(paymentStrategy);
        when(paymentRepository.save(payment)).thenReturn(payment);

        // Act
        Long result = paymentService.createPayment(paymentRequest);

        // Assert
        assertEquals(1L, result);
        verify(paymentStrategy).processPayment(payment);
        verify(paymentRepository).save(payment);
        verify(paymentProducer, never()).sendPaymentConfirmation(any());
    }

    @Test
    void createPayment_WhenRequestIsNull_ShouldThrowException() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> paymentService.createPayment(null)
        );
        verify(paymentRepository, never()).save(any());
        verify(paymentProducer, never()).sendPaymentConfirmation(any());
    }

    @Test
    void getPaymentUrlById_ShouldReturnUrlFromStrategy() throws StripeException {
        // Arrange
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentStrategyFactory.getStrategy(PaymentGatewayType.STRIPE_CARD)).thenReturn(paymentStrategy);
        when(paymentStrategy.getPaymentUrlById(1L)).thenReturn("https://stripe.com/session/123");

        // Act
        String result = paymentService.getPaymentUrlById(1L);

        // Assert
        assertEquals("https://stripe.com/session/123", result);
        verify(paymentRepository).findById(1L);
        verify(paymentStrategyFactory).getStrategy(PaymentGatewayType.STRIPE_CARD);
        verify(paymentStrategy).getPaymentUrlById(1L);
    }

    @Test
    void getPaymentUrlById_WhenIdIsNull_ShouldThrowException() throws StripeException {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> paymentService.getPaymentUrlById(null)
        );
        verify(paymentRepository, never()).findById(anyLong());
        verify(paymentStrategy, never()).getPaymentUrlById(anyLong());
    }

    @Test
    void getPaymentByOrderId_WhenPaymentExists_ShouldReturnPaymentResponse() {
        // Arrange
        when(paymentRepository.findByOrderId(100L)).thenReturn(Optional.of(payment));
        when(paymentMapper.toResponse(payment)).thenReturn(paymentResponse);

        // Act
        PaymentResponse result = paymentService.getPaymentByOrderId(100L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(100L, result.orderId());
        assertEquals(new BigDecimal("99.99"), result.totalPrice());
        assertEquals(PaymentStatus.UNPAID.name(), result.status());
        verify(paymentRepository).findByOrderId(100L);
        verify(paymentMapper).toResponse(payment);
    }

    @Test
    void getPaymentByOrderId_WhenPaymentDoesNotExist_ShouldThrowException() {
        // Arrange
        when(paymentRepository.findByOrderId(100L)).thenReturn(Optional.empty());

        // Act & Assert
        PaymentNotFoundException exception = assertThrows(
                PaymentNotFoundException.class,
                () -> paymentService.getPaymentByOrderId(100L)
        );
        assertTrue(exception.getMessage().contains("nebyla nalezena"));
        verify(paymentRepository).findByOrderId(100L);
        verify(paymentMapper, never()).toResponse(any());
    }

    @Test
    void getPaymentByOrderId_WhenOrderIdIsNull_ShouldThrowException() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> paymentService.getPaymentByOrderId(null)
        );
        verify(paymentRepository, never()).findByOrderId(anyLong());
        verify(paymentMapper, never()).toResponse(any());
    }
}