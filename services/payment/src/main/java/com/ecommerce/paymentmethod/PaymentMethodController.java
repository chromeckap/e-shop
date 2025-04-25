package com.ecommerce.paymentmethod;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment-methods")
@RequiredArgsConstructor
@Slf4j
public class PaymentMethodController {
    private final PaymentMethodService paymentMethodService;

    @GetMapping("/{id}")
    public ResponseEntity<PaymentMethodResponse> getPaymentMethodById(@PathVariable Long id) {
        log.info("Fetching payment method by ID: {}", id);
        PaymentMethodResponse response = paymentMethodService.getPaymentMethodById(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<PaymentMethodResponse>> getAllPaymentMethods() {
        log.info("Fetching all payment methods");
        List<PaymentMethodResponse> response = paymentMethodService.getAllPaymentMethods();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/active")
    public ResponseEntity<List<PaymentMethodResponse>> getActivePaymentMethods() {
        log.info("Fetching all active payment methods");
        List<PaymentMethodResponse> response = paymentMethodService.getActivePaymentMethods();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/types")
    public ResponseEntity<List<Map<String, String>>> getPaymentGatewayTypes() {
        log.info("Fetching all types of payment gateways");
        List<Map<String, String>> response = paymentMethodService.getPaymentGatewayTypes();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Long> createPaymentMethod(@RequestBody @Valid PaymentMethodRequest request) {
        log.info("Creating new payment method: {}", request);
        Long response = paymentMethodService.createPaymentMethod(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Long> updatePaymentMethod(
            @PathVariable Long id,
            @RequestBody @Valid PaymentMethodRequest request
    ) {
        log.info("Updating payment method with ID: {}", id);
        Long response = paymentMethodService.updatePaymentMethod(id, request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentMethodById(@PathVariable Long id) {
        log.info("Deleting payment method with ID: {}", id);
        paymentMethodService.deletePaymentMethodById(id);
        return ResponseEntity.noContent().build();
    }
}
