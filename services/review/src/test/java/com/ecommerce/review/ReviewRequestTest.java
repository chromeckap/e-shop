package com.ecommerce.review;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ReviewRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validReviewRequest_ShouldPassValidation() {
        // Arrange
        ReviewRequest request = new ReviewRequest(
                null, // optional id
                3,    // valid rating
                "This is a good product review", // valid text
                1L,   // valid user ID
                1L    // valid product ID
        );

        // Act
        Set<ConstraintViolation<ReviewRequest>> violations = validator.validate(request);

        // Assert
        assertTrue(violations.isEmpty(), "Valid review request should have no validation errors");
    }

    @Test
    void reviewRequest_RatingBelowMin_ShouldFail() {
        // Arrange
        ReviewRequest request = new ReviewRequest(
                null,
                0,
                "This is a good product review",
                1L,
                1L
        );

        // Act
        Set<ConstraintViolation<ReviewRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty(), "Review request with rating below 1 should fail validation");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Hodnocení musí být mezi 1 a 5.")));
    }

    @Test
    void reviewRequest_RatingAboveMax_ShouldFail() {
        // Arrange
        ReviewRequest request = new ReviewRequest(
                null,
                6,
                "This is a good product review",
                1L,
                1L
        );

        // Act
        Set<ConstraintViolation<ReviewRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty(), "Review request with rating above 5 should fail validation");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Hodnocení musí být mezi 1 a 5.")));
    }

    @Test
    void reviewRequest_NullText_ShouldFail() {
        // Arrange
        ReviewRequest request = new ReviewRequest(
                null,
                3,
                null,
                1L,
                1L
        );

        // Act
        Set<ConstraintViolation<ReviewRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty(), "Review request with null text should fail validation");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Text recenze nesmí být prázdný.")));
    }

    @Test
    void reviewRequest_EmptyText_ShouldFail() {
        // Arrange
        ReviewRequest request = new ReviewRequest(
                null,
                3,
                "",
                1L,
                1L
        );

        // Act
        Set<ConstraintViolation<ReviewRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty(), "Review request with empty text should fail validation");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Text recenze nesmí být prázdný.")));
    }

    @Test
    void reviewRequest_NullUserId_ShouldFail() {
        // Arrange
        ReviewRequest request = new ReviewRequest(
                null,
                3,
                "This is a good product review",
                null,
                1L
        );

        // Act
        Set<ConstraintViolation<ReviewRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty(), "Review request with null user ID should fail validation");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("ID uživatele nesmí být prázdné.")));
    }

    @Test
    void reviewRequest_NullProductId_ShouldFail() {
        // Arrange
        ReviewRequest request = new ReviewRequest(
                null,
                3,
                "This is a good product review",
                1L,
                null
        );

        // Act
        Set<ConstraintViolation<ReviewRequest>> violations = validator.validate(request);

        // Assert
        assertFalse(violations.isEmpty(), "Review request with null product ID should fail validation");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("ID produktu nesmí být prázdné.")));
    }

    @Test
    void reviewRequest_RecordProperties_ShouldWorkCorrectly() {
        // Arrange
        Long id = 1L;
        int rating = 4;
        String text = "Excellent product!";
        Long userId = 10L;
        Long productId = 20L;

        // Act
        ReviewRequest request = new ReviewRequest(id, rating, text, userId, productId);

        // Assert
        assertEquals(id, request.id());
        assertEquals(rating, request.rating());
        assertEquals(text, request.text());
        assertEquals(userId, request.userId());
        assertEquals(productId, request.productId());
    }
}