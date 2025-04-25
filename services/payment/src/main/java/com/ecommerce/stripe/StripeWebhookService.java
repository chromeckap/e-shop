package com.ecommerce.stripe;

import com.ecommerce.feignclient.order.OrderClient;
import com.ecommerce.feignclient.order.OrderResponse;
import com.ecommerce.kafka.PaymentConfirmation;
import com.ecommerce.kafka.PaymentProducer;
import com.ecommerce.payment.PaymentRepository;
import com.ecommerce.payment.PaymentStatus;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookService {
    private final PaymentRepository paymentRepository;
    private final PaymentProducer paymentProducer;
    private final OrderClient orderClient;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    /**
     * Handles incoming Stripe webhook events.
     *
     * @param payload         the raw payload received from Stripe
     * @param signatureHeader the signature header received from Stripe
     */
    @Transactional
    public void handleWebhook(String payload, String signatureHeader) {
        Objects.requireNonNull(payload, "Obsah zprávy nesmí být prázdný.");
        Objects.requireNonNull(payload, "Podpis hlavičky nesmí být prázdný.");
        log.debug("Proccessing payload: {} with signature {}", payload, signatureHeader);

        try {
            Event event = Webhook.constructEvent(payload, signatureHeader, endpointSecret);
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElseThrow();
            StripePayment payment = paymentRepository.findPaymentBySessionId(session.getId());
            OrderResponse order = orderClient.getOrderById(payment.getOrderId());

            switch (event.getType()) {
                case "checkout.session.completed" -> {
                    payment.setStatus(PaymentStatus.PAID);
                    paymentProducer.sendPaymentSuccessful(
                            PaymentConfirmation.builder()
                                    .orderId(payment.getOrderId())
                                    .totalPrice(payment.getTotalPrice())
                                    .user(order.userDetails())
                                    .OrderCreateTime(order.createTime().format(DateTimeFormatter.ofPattern("d.M.yyyy")))
                                    .build()
                    );
                }
                case "checkout.session.expired" -> payment.setStatus(PaymentStatus.EXPIRED);
                default -> throw new InvalidParameterException("Unhandled event type: " + event.getType());
            }
            paymentRepository.save(payment);

        } catch (SignatureVerificationException e) {
            log.error("Stripe signature verification failed: ", e);
        }
    }
}
