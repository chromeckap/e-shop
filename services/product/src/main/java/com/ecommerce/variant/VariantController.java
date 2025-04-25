package com.ecommerce.variant;

import com.ecommerce.variant.purchase.CartItemRequest;
import com.ecommerce.variant.purchase.PurchaseRequest;
import com.ecommerce.variant.purchase.PurchaseResponse;
import com.ecommerce.variant.purchase.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/variants")
@RequiredArgsConstructor
@Validated
@Slf4j
public class VariantController {
    private final VariantService variantService;
    private final PurchaseService purchaseService;

    @GetMapping("/{id}")
    public ResponseEntity<VariantResponse> getVariantById(@PathVariable Long id) {
        log.info("Fetching variant with ID: {}", id);
        VariantResponse response = variantService.getVariantById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<Set<VariantResponse>> getVariantsByProductId(@PathVariable Long id) {
        log.info("Fetching variants for product with ID: {}", id);
        Set<VariantResponse> response = variantService.getVariantsByProductId(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Long> createVariant(@RequestBody @Valid VariantRequest request) {
        log.info("Creating new variant: {}", request);
        Long response = variantService.createVariant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Long> updateVariant(
            @PathVariable Long id,
            @RequestBody @Valid VariantRequest request
    ) {
        log.info("Updating variant with ID: {}", id);
        Long response = variantService.updateVariant(id, request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Variant> deleteVariantById(@PathVariable Long id) {
        log.info("Deleting variant with ID: {}", id);
        variantService.deleteVariantById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/batch")
    public ResponseEntity<List<PurchaseResponse>> getVariantsByCartItems(@RequestBody List<CartItemRequest> cartItems) {
        log.info("Batching variants by cart items: {}", cartItems);
        List<PurchaseResponse> response = variantService.getVariantsByCartItems(cartItems);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/purchase")
    public ResponseEntity<Set<PurchaseResponse>> purchaseVariants(
            @RequestBody @Valid Set<PurchaseRequest> request
    ) {
        log.info("Purchasing variants: {}", request);
        Set<PurchaseResponse> response = purchaseService.purchaseVariants(request);
        return ResponseEntity.ok(response);
    }
}
