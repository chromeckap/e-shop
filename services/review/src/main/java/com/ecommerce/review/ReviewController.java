package com.ecommerce.review;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable Long id) {
        log.info("Fetching review with ID: {}", id);
        ReviewResponse response = reviewService.getReviewById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ReviewResponse>> getReviewsByProductId(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(required = false, defaultValue = "id") String attribute,
            @PathVariable Long productId
    ) {
        log.info("Fetching reviews for product with ID {} with pagination: page {}, size {}, sort by {}", productId, pageNumber, pageSize, direction);
        Sort sort = Sort.by(Sort.Direction.fromString(direction), attribute);
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        Page<ReviewResponse> response = reviewService.getReviewsByProductId(productId, pageRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/summary/{productId}")
    public ResponseEntity<ProductRatingSummary> getSummaryByProductId(@PathVariable Long productId) {
        log.info("Fetching review summary for product with ID: {}", productId);
        ProductRatingSummary response = reviewService.getSummaryByProductId(productId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<Long> createReview(@RequestBody @Valid ReviewRequest request) {
        log.info("Creating review for product: {}", request);
        Long response = reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<Long> updateReview(
            @PathVariable Long id,
            @RequestBody @Valid ReviewRequest request
    ) {
        log.info("Updating review with ID: {}", id);
        Long response = reviewService.updateReview(id, request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReviewById(@PathVariable Long id) {
        log.info("Deleting review with ID: {}", id);
        reviewService.deleteReviewById(id);
        return ResponseEntity.noContent().build();
    }
}
