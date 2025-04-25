package com.ecommerce.review;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validReview_ShouldPassValidation() {
        // Arrange
        Review review = Review.builder()
                .rating(3)
                .text("This is a valid review")
                .userId(1L)
                .productId(1L)
                .build();

        // Act
        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        // Assert
        assertTrue(violations.isEmpty(), "Valid review should have no validation errors");
    }

    @Test
    void review_InvalidRatingBelowMin_ShouldFail() {
        // Arrange
        Review review = Review.builder()
                .rating(0)
                .text("This is a valid review")
                .userId(1L)
                .productId(1L)
                .build();

        // Act
        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        // Assert
        assertFalse(violations.isEmpty(), "Review with rating below 1 should fail validation");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Hodnocení musí být alespoň 1.")));
    }

    @Test
    void review_InvalidRatingAboveMax_ShouldFail() {
        // Arrange
        Review review = Review.builder()
                .rating(6)
                .text("This is a valid review")
                .userId(1L)
                .productId(1L)
                .build();

        // Act
        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        // Assert
        assertFalse(violations.isEmpty(), "Review with rating above 5 should fail validation");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Hodnocení musí být maximálně 5.")));
    }

    @Test
    void review_NullRating_ShouldFail() {
        // Arrange
        Review review = Review.builder()
                .text("This is a valid review")
                .userId(1L)
                .productId(1L)
                .build();

        // Act
        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        // Assert
        assertFalse(violations.isEmpty(), "Review with null rating should fail validation");
    }

    @Test
    void review_EmptyText_ShouldFail() {
        // Arrange
        Review review = Review.builder()
                .rating(3)
                .text("")
                .userId(1L)
                .productId(1L)
                .build();

        // Act
        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        // Assert
        assertFalse(violations.isEmpty(), "Review with empty text should fail validation");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Text recenze nesmí být prázdný.")));
    }

    @Test
    void review_NullText_ShouldFail() {
        // Arrange
        Review review = Review.builder()
                .rating(3)
                .userId(1L)
                .productId(1L)
                .build();

        // Act
        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        // Assert
        assertFalse(violations.isEmpty(), "Review with null text should fail validation");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Text recenze nesmí být prázdný.")));
    }

    @Test
    void review_NullUserId_ShouldFail() {
        // Arrange
        Review review = Review.builder()
                .rating(3)
                .text("This is a valid review")
                .productId(1L)
                .build();

        // Act
        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        // Assert
        assertFalse(violations.isEmpty(), "Review with null user ID should fail validation");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("ID uživatele nesmí být prázdné.")));
    }

    @Test
    void review_NullProductId_ShouldFail() {
        // Arrange
        Review review = Review.builder()
                .rating(3)
                .text("This is a valid review")
                .userId(1L)
                .build();

        // Act
        Set<ConstraintViolation<Review>> violations = validator.validate(review);

        // Assert
        assertFalse(violations.isEmpty(), "Review with null product ID should fail validation");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("ID produktu nesmí být prázdné.")));
    }

    @Test
    void review_AuditingFields_ShouldBeNullable() {
        // Arrange
        Review review = Review.builder()
                .rating(3)
                .text("This is a valid review")
                .userId(1L)
                .productId(1L)
                .build();

        // Act & Assert
        assertNull(review.getCreateTime(), "CreateTime should initially be null");
        assertNull(review.getUpdateDate(), "UpdateDate should initially be null");
    }

    @Test
    void review_BuilderPattern_ShouldWorkCorrectly() {
        // Arrange
        Long userId = 1L;
        Long productId = 2L;
        int rating = 4;
        String text = "Great product!";

        // Act
        Review review = Review.builder()
                .rating(rating)
                .text(text)
                .userId(userId)
                .productId(productId)
                .build();

        // Assert
        assertEquals(rating, review.getRating());
        assertEquals(text, review.getText());
        assertEquals(userId, review.getUserId());
        assertEquals(productId, review.getProductId());
    }
}