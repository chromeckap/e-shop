package com.ecommerce.review;

import com.ecommerce.exception.ReviewNotFoundException;
import com.ecommerce.feignclient.product.ProductClient;
import com.ecommerce.feignclient.product.ProductResponse;
import com.ecommerce.security.SecurityValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private ReviewValidator reviewValidator;

    @Mock
    private SecurityValidator securityValidator;

    @Mock
    private ProductClient productClient;

    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewService = new ReviewService(
                reviewRepository,
                reviewMapper,
                reviewValidator,
                securityValidator,
                productClient
        );
    }

    @Test
    void findReviewById_ExistingReview_ReturnsReview() {
        // Arrange
        Long reviewId = 1L;
        Review mockReview = createMockReview(reviewId);

        when(reviewRepository.findById(reviewId))
                .thenReturn(Optional.of(mockReview));

        // Act
        Review foundReview = reviewService.findReviewById(reviewId);

        // Assert
        assertNotNull(foundReview);
        assertEquals(reviewId, foundReview.getId());
        verify(reviewRepository).findById(reviewId);
    }

    @Test
    void findReviewById_NonExistingReview_ThrowsReviewNotFoundException() {
        // Arrange
        Long reviewId = 999L;

        when(reviewRepository.findById(reviewId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ReviewNotFoundException.class,
                () -> reviewService.findReviewById(reviewId),
                "Recenze s ID 999 nebyla nalezena."
        );
    }

    @Test
    void getReviewById_ExistingReview_ReturnsReviewResponse() {
        // Arrange
        Long reviewId = 1L;
        Review mockReview = createMockReview(reviewId);
        ReviewResponse mockResponse = createMockReviewResponse(reviewId);

        when(reviewRepository.findById(reviewId))
                .thenReturn(Optional.of(mockReview));
        when(reviewMapper.toResponse(mockReview))
                .thenReturn(mockResponse);

        // Act
        ReviewResponse response = reviewService.getReviewById(reviewId);

        // Assert
        assertNotNull(response);
        assertEquals(reviewId, response.id());
        verify(reviewRepository).findById(reviewId);
        verify(reviewMapper).toResponse(mockReview);
    }

    @Test
    void getReviewsByProductId_ReturnsPageOfReviews() {
        // Arrange
        Long productId = 1L;
        PageRequest pageRequest = PageRequest.of(0, 5);
        Review mockReview = createMockReview(1L);
        PageImpl<Review> mockPage = new PageImpl<>(Collections.singletonList(mockReview));
        ReviewResponse mockResponse = createMockReviewResponse(1L);

        when(reviewRepository.findAllByProductId(productId, pageRequest))
                .thenReturn(mockPage);
        when(reviewMapper.toResponse(any(Review.class)))
                .thenReturn(mockResponse);

        // Act
        Page<ReviewResponse> resultPage = reviewService.getReviewsByProductId(productId, pageRequest);

        // Assert
        assertNotNull(resultPage);
        assertFalse(resultPage.isEmpty());
        assertEquals(1, resultPage.getContent().size());
        verify(reviewRepository).findAllByProductId(productId, pageRequest);
    }

    @Test
    void getSummaryByProductId_CalculatesSummaryCorrectly() {
        // Arrange
        Long productId = 1L;

        when(reviewRepository.countAllByProductId(productId))
                .thenReturn(10);

        Map<Integer, Integer> ratingCounts = new HashMap<>();
        ratingCounts.put(1, 2);
        ratingCounts.put(2, 2);
        ratingCounts.put(3, 2);
        ratingCounts.put(4, 2);
        ratingCounts.put(5, 2);

        for (int rating = 1; rating <= 5; rating++) {
            when(reviewRepository.countByProductIdAndRating(productId, rating))
                    .thenReturn(ratingCounts.get(rating));
        }

        ProductRatingSummary mockSummary = ProductRatingSummary.builder()
                .totalRatingsCount(10)
                .averageRating(3.0)
                .ratingCounts(ratingCounts)
                .build();

        when(reviewMapper.toSummary(anyInt(), anyDouble(), any()))
                .thenReturn(mockSummary);

        // Act
        ProductRatingSummary summary = reviewService.getSummaryByProductId(productId);

        // Assert
        assertNotNull(summary);
        assertEquals(10, summary.totalRatingsCount());
        assertEquals(3.0, summary.averageRating(), 0.01);
        assertEquals(5, summary.ratingCounts().size());
    }

    @Test
    void createReview_ValidReview_ReturnsReviewId() {
        // Arrange
        ReviewRequest request = new ReviewRequest(
                null, 4, "Great product", 1L, 1L
        );
        Review review = createMockReview(1L);
        ProductResponse product = new ProductResponse(1L);

        doNothing().when(securityValidator).validateUserAccess(request.userId());
        doNothing().when(reviewValidator).validateReviewIsUnique(request);
        when(reviewMapper.toReview(request)).thenReturn(review);
        when(productClient.getProductById(request.productId())).thenReturn(product);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        // Act
        Long reviewId = reviewService.createReview(request);

        // Assert
        assertNotNull(reviewId);
        assertEquals(1L, reviewId);
        verify(securityValidator).validateUserAccess(request.userId());
        verify(reviewValidator).validateReviewIsUnique(request);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void updateReview_ExistingReview_ReturnsUpdatedReviewId() {
        // Arrange
        Long reviewId = 1L;
        ReviewRequest request = new ReviewRequest(
                reviewId, 5, "Updated review", 1L, 1L
        );


        Review existingReview = new Review();
        existingReview.setId(reviewId);
        existingReview.setUserId(1L);
        existingReview.setProductId(1L);

        Review updatedReview = new Review();
        updatedReview.setId(reviewId);
        updatedReview.setRating(5);
        updatedReview.setText("Updated review");
        updatedReview.setUserId(1L);
        updatedReview.setProductId(1L);

        // Mock product response
        ProductResponse productResponse = new ProductResponse(1L);

        // Setup method call expectations
        when(reviewRepository.findById(reviewId)).thenReturn(java.util.Optional.of(existingReview));
        doNothing().when(securityValidator).validateUserAccess(request.userId());
        when(reviewMapper.toReview(request)).thenReturn(updatedReview);
        when(productClient.getProductById(request.productId())).thenReturn(productResponse);
        when(reviewRepository.save(any(Review.class))).thenReturn(updatedReview);

        // Act
        Long returnedId = reviewService.updateReview(reviewId, request);

        // Assert
        org.junit.jupiter.api.Assertions.assertEquals(reviewId, returnedId);

        // Verify method interactions
        verify(reviewRepository).findById(reviewId);
        verify(securityValidator).validateUserAccess(request.userId());
        verify(productClient).getProductById(request.productId());
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void deleteReviewById_OwnReview_DeletesSuccessfully() {
        // Arrange
        Long reviewId = 1L;
        Review review = createMockReview(reviewId);
        review.setUserId(1L);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(securityValidator.userHasRoleAdmin()).thenReturn(false);
        doNothing().when(securityValidator).validateUserAccess(review.getUserId());
        doNothing().when(reviewRepository).delete(review);

        // Act
        reviewService.deleteReviewById(reviewId);

        // Assert
        verify(reviewRepository).findById(reviewId);
        verify(securityValidator).validateUserAccess(review.getUserId());
        verify(reviewRepository).delete(review);
    }

    // Helper methods to create mock objects
    private Review createMockReview(Long id) {
        Review review = new Review();
        review.setId(id);
        review.setRating(4);
        review.setText("Test review");
        review.setUserId(1L);
        review.setProductId(1L);
        review.setCreateTime(LocalDateTime.now());
        return review;
    }

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