package eshop.backend.response;

public record RatingSummary(
        int totalRatingsCount,
        double averageRating,
        int[] ratingCounts
) {}