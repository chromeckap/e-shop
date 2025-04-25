package com.ecommerce.recommendation;

import com.ecommerce.contentbased.ContentBasedService;
import com.ecommerce.feignclient.product.ProductOverviewResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class RecommendationController {
    private final ContentBasedService contentBasedService;
    private final RecommendationService recommendationService;

    @GetMapping("/{productId}")
    public ResponseEntity<List<ProductOverviewResponse>> getRecommendationsByProductId(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "8") int limit
    ) {
        log.info("Fetching recommendations for product ID: {}", productId);
        List<ProductOverviewResponse> response = contentBasedService.getRecommendations(productId, limit);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Void> refreshRecommendations() {
        recommendationService.refreshRecommendations();
        return ResponseEntity.ok().build();
    }
}