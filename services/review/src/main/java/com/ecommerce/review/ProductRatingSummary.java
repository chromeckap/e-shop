package com.ecommerce.review;

import lombok.Builder;

import java.util.Map;

@Builder
public record ProductRatingSummary(
        int totalRatingsCount,
        double averageRating,
        Map<Integer, Integer> ratingCounts
) {}
