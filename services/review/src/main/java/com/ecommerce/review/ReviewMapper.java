package com.ecommerce.review;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class ReviewMapper {
    public Review toReview(@NonNull ReviewRequest request) {
        log.debug("Mapping ReviewRequest to Review: {}", request);
        return Review.builder()
                .rating(request.rating())
                .text(request.text())
                .build();
    }

    public ReviewResponse toResponse(@NonNull Review review) {
        log.debug("Mapping Review to ReviewResponse: {}", review);
        return ReviewResponse.builder()
                .id(review.getId())
                .rating(review.getRating())
                .text(review.getText())
                .userId(review.getUserId())
                .productId(review.getProductId())
                .createTime(review.getCreateTime())
                .updateTime(review.getUpdateDate())
                .build();
    }

    public ProductRatingSummary toSummary(
            int totalReviews,
            double averageRating,
            Map<Integer, Integer> ratingCounts
    ) {
        log.debug("Creating ProductRatingSummary: totalReviews={}, averageRating={}", totalReviews, averageRating);
        return ProductRatingSummary.builder()
                .totalRatingsCount(totalReviews)
                .averageRating(averageRating)
                .ratingCounts(ratingCounts)
                .build();
    }
}
