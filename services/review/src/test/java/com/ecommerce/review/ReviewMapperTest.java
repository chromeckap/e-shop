package com.ecommerce.review;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ReviewMapperTest {

    private ReviewMapper reviewMapper;

    @BeforeEach
    void setUp() {
        reviewMapper = new ReviewMapper();
    }

    @Test
    void toReview_ValidRequest_MapsCorrectly() {
        // Arrange
        ReviewRequest request = new ReviewRequest(
                null, // id is null for new review
                4,
                "Great product!",
                1L,
                2L
        );

        // Act
        Review review = reviewMapper.toReview(request);

        // Assert
        assertNotNull(review);
        assertEquals(request.rating(), review.getRating());
        assertEquals(request.text(), review.getText());
        // Note: userId and productId are not mapped in the current implementation
    }

    @Test
    void toReview_NullRequest_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> reviewMapper.toReview(null),
                "Should throw NullPointerException for null request"
        );
    }

    @Test
    void toResponse_ValidReview_MapsCorrectly() {
        // Arrange
        Review review = new Review();
        review.setId(1L);
        review.setRating(4);
        review.setText("Excellent product");
        review.setUserId(10L);
        review.setProductId(20L);

        LocalDateTime createTime = LocalDateTime.now();
        LocalDateTime updateTime = createTime.plusDays(1);

        review.setCreateTime(createTime);
        review.setUpdateDate(updateTime);

        // Act
        ReviewResponse response = reviewMapper.toResponse(review);

        // Assert
        assertNotNull(response);
        assertEquals(review.getId(), response.id());
        assertEquals(review.getRating(), response.rating());
        assertEquals(review.getText(), response.text());
        assertEquals(review.getUserId(), response.userId());
        assertEquals(review.getProductId(), response.productId());
        assertEquals(review.getCreateTime(), response.createTime());
        assertEquals(review.getUpdateDate(), response.updateTime());
    }

    @Test
    void toResponse_NullReview_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> reviewMapper.toResponse(null),
                "Should throw NullPointerException for null review"
        );
    }

    @Test
    void toSummary_ValidInput_MapsCorrectly() {
        // Arrange
        int totalReviews = 10;
        double averageRating = 4.5;

        Map<Integer, Integer> ratingCounts = new HashMap<>();
        ratingCounts.put(1, 1);
        ratingCounts.put(2, 2);
        ratingCounts.put(3, 2);
        ratingCounts.put(4, 3);
        ratingCounts.put(5, 2);

        // Act
        ProductRatingSummary summary = reviewMapper.toSummary(
                totalReviews,
                averageRating,
                ratingCounts
        );

        // Assert
        assertNotNull(summary);
        assertEquals(totalReviews, summary.totalRatingsCount());
        assertEquals(averageRating, summary.averageRating(), 0.001);
        assertEquals(ratingCounts, summary.ratingCounts());
    }

    @Test
    void toSummary_EmptyRatingCounts_StillWorks() {
        // Arrange
        int totalReviews = 0;
        double averageRating = 0.0;
        Map<Integer, Integer> ratingCounts = new HashMap<>();

        // Act
        ProductRatingSummary summary = reviewMapper.toSummary(
                totalReviews,
                averageRating,
                ratingCounts
        );

        // Assert
        assertNotNull(summary);
        assertEquals(totalReviews, summary.totalRatingsCount());
        assertEquals(averageRating, summary.averageRating(), 0.001);
        assertTrue(summary.ratingCounts().isEmpty());
    }
}