package com.ecommerce.deliverymethod;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/delivery-methods")
@RequiredArgsConstructor
@Validated
@Slf4j
public class DeliveryMethodController {
    private final DeliveryMethodService deliveryMethodService;

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryMethodResponse> getDeliveryMethodById(@PathVariable Long id) {
        log.info("Fetching delivery method by ID: {}", id);
        DeliveryMethodResponse response = deliveryMethodService.getDeliveryMethodById(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<DeliveryMethodResponse>> getAllDeliveryMethods() {
        log.info("Fetching all delivery methods");
        List<DeliveryMethodResponse> response = deliveryMethodService.getAllDeliveryMethods();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/active")
    public ResponseEntity<List<DeliveryMethodResponse>> getActiveDeliveryMethods() {
        log.info("Fetching all active delivery methods");
        List<DeliveryMethodResponse> response = deliveryMethodService.getActiveDeliveryMethods();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/widget/{courierType}")
    public ResponseEntity<String> getCourierWidgetUrl(@PathVariable String courierType) {
        log.info("Fetching courier widget url: {}", courierType);
        String response = deliveryMethodService.getCourierWidgetUrl(courierType);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/couriers")
    public ResponseEntity<List<Map<String, String>>> getCourierTypes() {
        log.info("Fetching all types of couriers");
        List<Map<String, String>> response = deliveryMethodService.getCourierTypes();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Long> createDeliveryMethod(@RequestBody @Valid DeliveryMethodRequest request) {
        log.info("Creating new delivery method: {}", request);
        Long response = deliveryMethodService.createDeliveryMethod(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Long> updateDeliveryMethod(
            @PathVariable Long id,
            @RequestBody @Valid DeliveryMethodRequest request
    ) {
        log.info("Updating delivery method with ID: {}", id);
        Long response = deliveryMethodService.updateDeliveryMethod(id, request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeliveryMethodById(@PathVariable Long id) {
        log.info("Deleting delivery method with ID: {}", id);
        deliveryMethodService.deleteDeliveryMethodById(id);
        return ResponseEntity.noContent().build();
    }
}
