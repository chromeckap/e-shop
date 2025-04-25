package com.ecommerce.payment;

import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Long> createPayment(@RequestBody @Valid PaymentRequest request) throws StripeException {
        log.info("Creating payment with details: {}", request);
        Long response = paymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getPaymentUrlById(@PathVariable Long id) throws StripeException {
        log.info("Fetching payment URL for payment ID: {}", id);
        String response = paymentService.getPaymentUrlById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(@PathVariable Long orderId) {
        log.info("Fetching payment for order ID: {}", orderId);
        PaymentResponse response = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(response);
    }
}
