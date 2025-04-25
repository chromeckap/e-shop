package com.ecommerce.review;

import com.ecommerce.exception.ReviewExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewValidatorTest {

    @Mock
    private ReviewRepository reviewRepository;

    private ReviewValidator reviewValidator;

    @BeforeEach
    void setUp() {
        reviewValidator = new ReviewValidator(reviewRepository);
    }

    @Test
    void validateReviewIsUnique_NoExistingReview_ShouldPass() {
        // Arrange
        ReviewRequest request = new ReviewRequest(
                null,
                4,
                "Great product",
                1L,
                2L
        );

        when(reviewRepository.existsByUserIdAndProductId(request.userId(), request.productId()))
                .thenReturn(false);

        // Act & Assert
        assertDoesNotThrow(() -> reviewValidator.validateReviewIsUnique(request),
                "Validator should not throw exception when no existing review");

        verify(reviewRepository).existsByUserIdAndProductId(request.userId(), request.productId());
    }

    @Test
    void validateReviewIsUnique_ExistingReview_ShouldThrowReviewExistsException() {
        // Arrange
        ReviewRequest request = new ReviewRequest(
                null,
                4,
                "Great product",
                1L,
                2L
        );

        when(reviewRepository.existsByUserIdAndProductId(request.userId(), request.productId()))
                .thenReturn(true);

        // Act & Assert
        ReviewExistsException exception = assertThrows(
                ReviewExistsException.class,
                () -> reviewValidator.validateReviewIsUnique(request),
                "Validator should throw ReviewExistsException when review already exists"
        );

        assertEquals(
                "Pro tento produkt jste již recenzi napsal.",
                exception.getMessage(),
                "Exception message should match expected text"
        );

        verify(reviewRepository).existsByUserIdAndProductId(request.userId(), request.productId());
    }

    @Test
    void validateReviewIsUnique_MultipleChecks_ShouldWorkConsistently() {
        // Arrange
        ReviewRequest request1 = new ReviewRequest(
                null,
                4,
                "First review",
                1L,
                2L
        );

        ReviewRequest request2 = new ReviewRequest(
                null,
                5,
                "Second review",
                1L,
                2L
        );

        // First request - no existing review
        when(reviewRepository.existsByUserIdAndProductId(request1.userId(), request1.productId()))
                .thenReturn(false);

        // Second request - existing review
        when(reviewRepository.existsByUserIdAndProductId(request2.userId(), request2.productId()))
                .thenReturn(true);

        // Act & Assert

        ReviewExistsException exception = assertThrows(
                ReviewExistsException.class,
                () -> reviewValidator.validateReviewIsUnique(request2),
                "Second request should throw exception"
        );

        assertEquals(
                "Pro tento produkt jste již recenzi napsal.",
                exception.getMessage(),
                "Exception message should match expected text"
        );
    }

    @Test
    void validateReviewIsUnique_DifferentProducts_ShouldPass() {
        // Arrange
        ReviewRequest request1 = new ReviewRequest(
                null,
                4,
                "Review for Product 1",
                1L,
                1L
        );

        ReviewRequest request2 = new ReviewRequest(
                null,
                5,
                "Review for Product 2",
                1L,
                2L
        );

        when(reviewRepository.existsByUserIdAndProductId(request1.userId(), request1.productId()))
                .thenReturn(false);
        when(reviewRepository.existsByUserIdAndProductId(request2.userId(), request2.productId()))
                .thenReturn(false);

        // Act & Assert
        assertDoesNotThrow(() -> reviewValidator.validateReviewIsUnique(request1),
                "First request should pass validation");
        assertDoesNotThrow(() -> reviewValidator.validateReviewIsUnique(request2),
                "Second request should pass validation");

        verify(reviewRepository, times(2))
                .existsByUserIdAndProductId(anyLong(), anyLong());
    }
}