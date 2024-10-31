package eshop.backend.controller;

import eshop.backend.exception.VariantNotFoundException;
import eshop.backend.response.VariantResponse;
import eshop.backend.service.VariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/variants")
@RequiredArgsConstructor
public class VariantController {
    private final VariantService variantService;

    @GetMapping("/{id}")
    public ResponseEntity<VariantResponse> read(@PathVariable Long id) throws VariantNotFoundException {
        var product = variantService.getVariant(id);
        return ResponseEntity.ok(product);
    }
}
