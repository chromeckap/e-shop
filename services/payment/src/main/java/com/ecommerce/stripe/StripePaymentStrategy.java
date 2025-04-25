package com.ecommerce.stripe;

import com.ecommerce.payment.PaymentRepository;
import com.ecommerce.payment.PaymentStatus;
import com.ecommerce.strategy.PaymentGatewayType;
import com.ecommerce.strategy.PaymentGatewayHandler;
import com.ecommerce.strategy.PaymentStrategy;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.stripe.param.checkout.SessionCreateParams.Locale.CS;
import static com.stripe.param.checkout.SessionCreateParams.Mode.PAYMENT;
import static com.stripe.param.checkout.SessionCreateParams.PaymentMethodType.CARD;

@Component
@RequiredArgsConstructor
@Slf4j
@PaymentGatewayHandler(PaymentGatewayType.STRIPE_CARD)
public class StripePaymentStrategy implements PaymentStrategy<StripePayment> {
    private final PaymentRepository paymentRepository;

    private static final String DEFAULT_CURRENCY = "czk";
    private static final String SUCCESS_URL = "http://localhost:4200";
    private static final String CANCEL_URL = "http://localhost:4200";

    @Override
    public void processPayment(StripePayment payment) {
        try {
            SessionCreateParams params = this.createSessionParams(payment.getTotalPrice());
            Session session = Session.create(params);

            payment.setSessionId(session.getId());
            payment.setStatus(PaymentStatus.UNPAID);

            log.info("Stripe platba úspěšně vytvořena.");

        } catch (StripeException exception) {
            log.error("Chyba při vytvoření platby.", exception);
        }
    }

    @Override
    public String getPaymentUrlById(Long id) throws StripeException {
        StripePayment payment = (StripePayment) paymentRepository.findById(id)
                .orElseThrow();

        Session retrieve = Session.retrieve(payment.getSessionId());

        return switch (payment.getStatus()) {
            case UNPAID -> retrieve.getUrl();
            case PAID -> throw new RuntimeException("už je zaplacena");
            case EXPIRED -> ".. new session";
            default -> throw new RuntimeException();
        };
    }

    private SessionCreateParams createSessionParams(BigDecimal totalPrice) {
        return SessionCreateParams.builder()
                .setMode(PAYMENT)
                .addPaymentMethodType(CARD)
                .setSuccessUrl(SUCCESS_URL)
                .setCancelUrl(CANCEL_URL)
                .setLocale(CS)
                .addLineItem(this.createPriceLineItem(totalPrice))
                .build();
    }

    private SessionCreateParams.LineItem createPriceLineItem(BigDecimal totalPrice) {
        SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName("Celková částka k zaplacení")
                .build();

        SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency(DEFAULT_CURRENCY)
                .setUnitAmountDecimal(totalPrice.multiply(BigDecimal.valueOf(100)))
                .setProductData(productData)
                .build();

        return SessionCreateParams.LineItem.builder()
                .setQuantity(1L)
                .setPriceData(priceData)
                .build();
    }
}
