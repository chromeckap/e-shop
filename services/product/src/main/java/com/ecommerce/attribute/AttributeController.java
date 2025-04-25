package com.ecommerce.attribute;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/attributes")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AttributeController {
    private final AttributeService attributeService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<AttributeResponse> getAttributeById(@PathVariable Long id) {
        log.info("Fetching attribute with ID: {}", id);
        AttributeResponse response = attributeService.getAttributeById(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Set<AttributeResponse>> getAllAttributes() {
        log.info("Fetching all attributes");
        Set<AttributeResponse> response = attributeService.getAllAttributes();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Long> createAttribute(@RequestBody @Valid AttributeRequest request) {
        log.info("Creating new attribute: {}", request);
        Long response = attributeService.createAttribute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Long> updateAttribute(
            @PathVariable Long id,
            @RequestBody @Valid AttributeRequest request
    ) {
        log.info("Updating attribute with ID: {}", id);
        Long response = attributeService.updateAttribute(id, request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttributeById(@PathVariable Long id) {
        log.info("Deleting attribute with ID: {}", id);
        attributeService.deleteAttributeById(id);
        return ResponseEntity.noContent().build();
    }
}