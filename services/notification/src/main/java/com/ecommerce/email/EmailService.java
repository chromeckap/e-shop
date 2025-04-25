package com.ecommerce.email;

import com.ecommerce.kafka.order.OrderConfirmation;
import com.ecommerce.kafka.order.User;
import com.ecommerce.kafka.payment.PaymentConfirmation;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_RELATED;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${mail.sender.username}")
    private String sender;


    /**
     * Sends an order confirmation email to the customer.
     *
     * @param orderConfirmation Object containing order details.
     */
    @Async
    public void sendOrderConfirmationEmail(OrderConfirmation orderConfirmation) {
        Objects.requireNonNull(orderConfirmation, "OrderConfirmation nesmí být prázdné.");
        log.debug("Preparing order confirmation email for orderId: {}", orderConfirmation.orderId());

        Map<String, Object> variables = Map.of(
                "orderId", orderConfirmation.orderId(),
                "orderDate", orderConfirmation.orderDate(),
                "user", orderConfirmation.user(),
                "products", orderConfirmation.products(),
                "additionalCosts", orderConfirmation.additionalCosts(),
                "totalPrice", orderConfirmation.totalPrice()
        );
        sendEmail(orderConfirmation.user().email(), EmailTemplates.ORDER_CONFIRMATION, variables);
    }

    /**
     * Sends a payment creation email to the customer.
     *
     * @param paymentConfirmation Object containing payment details.
     */
    @Async
    public void sendPaymentCreationEmail(PaymentConfirmation paymentConfirmation) {
        Objects.requireNonNull(paymentConfirmation, "PaymentConfirmation nesmí být prázdný.");
        log.debug("Preparing payment creation email for orderId: {}", paymentConfirmation.orderId());

        Map<String, Object> variables = Map.of(
                "orderId", paymentConfirmation.orderId(),
                "paymentMethod", paymentConfirmation.paymentMethodName(),
                "sessionUri", paymentConfirmation.sessionUri(),
                "user", paymentConfirmation.user(),
                "totalPrice", paymentConfirmation.totalPrice(),
                "orderCreateTime", paymentConfirmation.OrderCreateTime()
        );
        sendEmail(paymentConfirmation.user().email(), EmailTemplates.PAYMENT_CREATED, variables);

        sendPaymentSuccessfulEmail(paymentConfirmation);
    }

    /**
     * Sends a payment successful email to the customer.
     *
     * @param paymentConfirmation Object containing payment confirmation details.
     */
    @Async
    public void sendPaymentSuccessfulEmail(PaymentConfirmation paymentConfirmation) {
        Objects.requireNonNull(paymentConfirmation, "PaymentConfirmation nesmí být prázdný.");
        log.debug("Preparing payment successful email for orderId: {}", paymentConfirmation.orderId());

        Map<String, Object> variables = Map.of(
                "user", paymentConfirmation.user(),
                "orderId", paymentConfirmation.orderId(),
                "totalPrice", paymentConfirmation.totalPrice(),
                "orderCreateTime", paymentConfirmation.OrderCreateTime()
        );
        sendEmail(paymentConfirmation.user().email(), EmailTemplates.PAYMENT_SUCCESSFUL, variables);
    }

    /**
     * Generic method for sending emails using predefined templates.
     *
     * @param recipient Email address of the recipient.
     * @param template  Email template to be used.
     * @param variables Variables to be injected into the email template.
     */
    private void sendEmail(String recipient, EmailTemplates template, Map<String, Object> variables) {
        Objects.requireNonNull(recipient, "E-mail příjemce nesmí být prázdný.");
        Objects.requireNonNull(template, "Šablona nesmí být prázdná.");
        Objects.requireNonNull(variables, "Hodnoty v e-mailu nesmí být prázdné.");

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MULTIPART_MODE_RELATED, UTF_8.name());
            helper.setFrom(sender);
            helper.setTo(recipient);
            helper.setSubject(template.getSubject());

            Context context = new Context();
            context.setVariables(variables);
            String html = templateEngine.process(template.getTemplate(), context);
            helper.setText(html, true);

            javaMailSender.send(mimeMessage);
            log.info("Email successfully sent to {}", recipient);
        } catch (MessagingException e) {
            log.error("Email could not be sent to {}", recipient, e);
        }
    }


}
