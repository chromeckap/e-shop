package com.ecommerce.review;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    private ReviewController reviewController;

    @BeforeEach
    void setUp() {
        reviewController = new ReviewController(reviewService);
    }

    @Test
    void getReviewById_ExistingReview_ReturnsReviewResponse() {
        // Arrange
        Long reviewId = 1L;
        ReviewResponse mockResponse = createMockReviewResponse(reviewId);

        when(reviewService.getReviewById(reviewId))
                .thenReturn(mockResponse);

        // Act
        ResponseEntity<ReviewResponse> response = reviewController.getReviewById(reviewId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(reviewService).getReviewById(reviewId);
    }

    @Test
    void getReviewsByProductId_ReturnsPageOfReviews() {
        // Arrange
        Long productId = 1L;
        int pageNumber = 0;
        int pageSize = 5;
        String direction = "DESC";
        String attribute = "id";

        ReviewResponse mockReviewResponse = createMockReviewResponse(1L);
        Page<ReviewResponse> mockPage = new PageImpl<>(Collections.singletonList(mockReviewResponse));

        when(reviewService.getReviewsByProductId(
                eq(productId),
                argThat(pageRequest ->
                        pageRequest.getPageNumber() == pageNumber &&
                                pageRequest.getPageSize() == pageSize &&
                                pageRequest.getSort().getOrderFor("id").getDirection() == Sort.Direction.DESC
                )
        )).thenReturn(mockPage);

        // Act
        ResponseEntity<Page<ReviewResponse>> response = reviewController.getReviewsByProductId(
                pageNumber, pageSize, direction, attribute, productId
        );

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().getContent().size());

        verify(reviewService).getReviewsByProductId(
                eq(productId),
                argThat(pageRequest ->
                        pageRequest.getPageNumber() == pageNumber &&
                                pageRequest.getPageSize() == pageSize &&
                                pageRequest.getSort().getOrderFor("id").getDirection() == Sort.Direction.DESC
                )
        );
    }

    @Test
    void getSummaryByProductId_ReturnsSummary() {
        // Arrange
        Long productId = 1L;

        Map<Integer, Integer> ratingCounts = new HashMap<>();
        ratingCounts.put(1, 2);
        ratingCounts.put(2, 3);
        ratingCounts.put(3, 4);
        ratingCounts.put(4, 5);
        ratingCounts.put(5, 6);

        ProductRatingSummary mockSummary = ProductRatingSummary.builder()
                .totalRatingsCount(20)
                .averageRating(3.5)
                .ratingCounts(ratingCounts)
                .build();

        when(reviewService.getSummaryByProductId(productId))
                .thenReturn(mockSummary);

        // Act
        ResponseEntity<ProductRatingSummary> response = reviewController.getSummaryByProductId(productId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockSummary, response.getBody());
        verify(reviewService).getSummaryByProductId(productId);
    }

    @Test
    void createReview_ValidReview_ReturnsCreatedReviewId() {
        // Arrange
        ReviewRequest request = new ReviewRequest(
                null, 4, "Great product", 1L, 1L
        );
        Long createdReviewId = 1L;

        when(reviewService.createReview(request))
                .thenReturn(createdReviewId);

        // Act
        ResponseEntity<Long> response = reviewController.createReview(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdReviewId, response.getBody());
        verify(reviewService).createReview(request);
    }

    @Test
    void updateReview_ExistingReview_ReturnsUpdatedReviewId() {
        // Arrange
        Long reviewId = 1L;
        ReviewRequest request = new ReviewRequest(
                reviewId, 5, "Updated review", 1L, 1L
        );
        Long updatedReviewId = 1L;

        when(reviewService.updateReview(reviewId, request))
                .thenReturn(updatedReviewId);

        // Act
        ResponseEntity<Long> response = reviewController.updateReview(reviewId, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedReviewId, response.getBody());
        verify(reviewService).updateReview(reviewId, request);
    }

    @Test
    void deleteReviewById_ExistingReview_ReturnsNoContent() {
        // Arrange
        Long reviewId = 1L;
        doNothing().when(reviewService).deleteReviewById(reviewId);

        // Act
        ResponseEntity<Void> response = reviewController.deleteReviewById(reviewId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(reviewService).deleteReviewById(reviewId);
    }

    // Helper method to create mock ReviewResponse
    private ReviewResponse createMockReviewResponse(Long id) {
        return ReviewResponse.builder()
                .id(id)
                .rating(4)
                .text("Test review")
                .userId(1L)
                .productId(1L)
                .createTime(LocalDateTime.now())
                .build();
    }
}