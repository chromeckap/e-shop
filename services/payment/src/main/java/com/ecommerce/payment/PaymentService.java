package com.ecommerce.payment;

import com.ecommerce.exception.PaymentNotFoundException;
import com.ecommerce.kafka.PaymentConfirmation;
import com.ecommerce.kafka.PaymentProducer;
import com.ecommerce.paymentmethod.PaymentMethod;
import com.ecommerce.paymentmethod.PaymentMethodService;
import com.ecommerce.strategy.PaymentGatewayType;
import com.ecommerce.strategy.PaymentStrategy;
import com.ecommerce.strategy.PaymentStrategyFactory;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentProducer paymentProducer;
    private final PaymentStrategyFactory paymentStrategyFactory;
    private final PaymentMethodService paymentMethodService;

    /**
     * Finds a payment entity by its ID.
     *
     * @param id the payment ID
     * @return the Payment entity
     * @throws PaymentNotFoundException if the payment is not found
     */
    @Transactional(readOnly = true)
    public Payment findPaymentEntityById(Long id) {
        Objects.requireNonNull(id, "ID platby nesmí být prázdné.");
        log.debug("Attempting to find payment with ID: {}", id);

        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(
                        String.format("Platba s ID %s nebyla nalezena.", id)
                ));
    }

    /**
     * Creates a new payment using the provided payment request.
     * The method uses a payment strategy to process the payment and then saves the payment.
     * A payment confirmation message is also sent via Kafka.
     *
     * @param request the payment request containing necessary details
     * @return the ID of the saved payment
     */
    @Transactional
    public Long createPayment(PaymentRequest request) throws StripeException {
        Objects.requireNonNull(request, "Požadavek na platbu nesmí být prázdný.");
        log.debug("Creating payment for order ID: {}", request.orderId());

        PaymentMethod paymentMethod = paymentMethodService.findPaymentMethodById(request.paymentMethodId());
        PaymentGatewayType paymentGatewayType = paymentMethod.getGatewayType();
        Payment payment = paymentMapper.toPayment(request, paymentGatewayType);
        payment.setMethod(paymentMethod);

        PaymentStrategy<Payment> strategy = paymentStrategyFactory.getStrategy(paymentGatewayType);
        strategy.processPayment(payment);

        Payment savedPayment = paymentRepository.save(payment);
        log.debug("Payment saved with ID: {}", savedPayment.getId());

        if (paymentGatewayType.equals(PaymentGatewayType.STRIPE_CARD)) {
            PaymentConfirmation confirmation = PaymentConfirmation.builder()
                    .orderId(request.orderId())
                    .totalPrice(request.totalPrice())
                    .user(request.user())
                    .paymentMethodName(paymentMethod.getName())
                    .sessionUri(strategy.getPaymentUrlById(payment.getId()))
                    .OrderCreateTime(request.OrderCreateTime())
                    .build();
            paymentProducer.sendPaymentConfirmation(confirmation);
            log.debug("Payment confirmation sent for order ID: {}", request.orderId());
        }

        return savedPayment.getId();
    }

    /**
     * Retrieves the payment URL for a given payment ID.
     * This method delegates to the appropriate payment strategy based on the payment method.
     *
     * @param id the payment ID
     * @return the payment URL as a String
     * @throws StripeException if there is an error communicating with Stripe
     */
    @Transactional(readOnly = true)
    public String getPaymentUrlById(Long id) throws StripeException {
        Objects.requireNonNull(id, "ID platby nesmí být prázdné.");
        log.debug("Retrieving payment URL for payment ID: {}", id);

        Payment payment = this.findPaymentEntityById(id);
        PaymentStrategy<Payment> strategy = paymentStrategyFactory.getStrategy(
                payment.getMethod().getGatewayType()
        );

        log.info("Retrieved payment URL for payment ID: {}", id);
        return strategy.getPaymentUrlById(id);
    }

    /**
     * Retrieves a payment by it order ID.
     *
     * @param orderId the ID of the payment's order
     * @return the payment response
     * @throws PaymentNotFoundException if no payment method is found
     */
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        Objects.requireNonNull(orderId, "ID objednávky nesmí být prázdné.");
        log.debug("Fetching payment response for order ID: {}", orderId);

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException(
                        String.format("Platba s ID objednávky %s nebyla nalezena.", orderId)
                ));
        return paymentMapper.toResponse(payment);
    }
}
