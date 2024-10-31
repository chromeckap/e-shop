package eshop.backend.controller;

import eshop.backend.exception.ProductNotFoundException;
import eshop.backend.exception.ReviewNotFoundException;
import eshop.backend.exception.UserNotFoundException;
import eshop.backend.model.Review;
import eshop.backend.request.ReviewRequest;
import eshop.backend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/create")
    public ResponseEntity<Review> create(@RequestBody ReviewRequest request) throws ProductNotFoundException, UserNotFoundException {
        Review review = reviewService.create(request);

        return ResponseEntity.ok(review);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<Review> read(@PathVariable Long reviewId) throws ReviewNotFoundException {
        //Review review = reviewService.read(reviewId);
        var review = new Review();

        return ResponseEntity.ok(review);
    }

    //@PreAuthorize("#request.userId == principal") todo + id?
    @PutMapping("/{reviewId}")
    public ResponseEntity<Review> update(@PathVariable Long reviewId, @RequestBody ReviewRequest request) throws ReviewNotFoundException {
        Review updatedReview = reviewService.update(request);
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> delete(@PathVariable Long reviewId) throws ReviewNotFoundException {
        reviewService.delete(reviewId);
        return ResponseEntity.noContent().build();
    }
}
