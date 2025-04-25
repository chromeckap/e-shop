package com.ecommerce.strategy;

import com.ecommerce.cod.CODPaymentStrategy;
import com.ecommerce.payment.Payment;
import com.ecommerce.payment.PaymentRepository;
import com.ecommerce.stripe.StripePaymentStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentStrategyFactoryTest {

    private PaymentStrategyFactory paymentStrategyFactory;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private PaymentRepository paymentRepository;

    @BeforeEach
    void setUp() {
        // Create strategies for testing
        StripePaymentStrategy stripeStrategy = new StripePaymentStrategy(paymentRepository);
        CODPaymentStrategy codStrategy = new CODPaymentStrategy();

        // Prepare mock beans
        Map<String, Object> mockBeans = new HashMap<>();
        mockBeans.put("stripeStrategy", stripeStrategy);
        mockBeans.put("codStrategy", codStrategy);

        // Mock application context to return our strategies
        when(applicationContext.getBeansWithAnnotation(PaymentGatewayHandler.class)).thenReturn(mockBeans);

        // Create factory and initialize
        paymentStrategyFactory = new PaymentStrategyFactory(applicationContext);
        paymentStrategyFactory.init();
    }

    @Test
    void testGetStrategy_Stripe() {
        PaymentStrategy<Payment> strategy = paymentStrategyFactory.getStrategy(PaymentGatewayType.STRIPE_CARD);
        assertNotNull(strategy);
        assertInstanceOf(StripePaymentStrategy.class, strategy);
    }

    @Test
    void testGetStrategy_CashOnDelivery() {
        PaymentStrategy<Payment> strategy = paymentStrategyFactory.getStrategy(PaymentGatewayType.CASH_ON_DELIVERY);
        assertNotNull(strategy);
        assertInstanceOf(CODPaymentStrategy.class, strategy);
    }

    @Test
    void testGetStrategy_NonExistentType() {
        assertThrows(IllegalArgumentException.class, () ->
                paymentStrategyFactory.getStrategy(null)
        );
    }
}