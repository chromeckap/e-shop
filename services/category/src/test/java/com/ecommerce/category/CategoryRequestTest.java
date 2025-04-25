package com.ecommerce.category;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CategoryRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testValidCategoryRequest() {
        CategoryRequest request = new CategoryRequest(
                1L,
                "Test Category",
                "Test Description",
                null
        );

        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Validní kategorie by neměla mít žádné porušení omezení");

        assertEquals(1L, request.id());
        assertEquals("Test Category", request.name());
        assertEquals("Test Description", request.description());
        assertNull(request.parentId());
    }

    @Test
    void testEmptyName() {
        CategoryRequest request = new CategoryRequest(
                1L,
                "",
                "Test Description",
                null
        );

        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Měla by být zjištěna jedna chyba validace");

        ConstraintViolation<CategoryRequest> violation = violations.iterator().next();
        assertEquals("name", violation.getPropertyPath().toString());
        assertEquals("Jméno kategorie nesmí být prázdné.", violation.getMessage());
    }

    @Test
    void testNullName() {
        CategoryRequest request = new CategoryRequest(
                1L,
                null,
                "Test Description",
                null
        );

        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Měla by být zjištěna jedna chyba validace");

        ConstraintViolation<CategoryRequest> violation = violations.iterator().next();
        assertEquals("name", violation.getPropertyPath().toString());
        assertEquals("Jméno kategorie nesmí být prázdné.", violation.getMessage());
    }

    @Test
    void testNameTooLong() {
        String longName = "a".repeat(101);
        CategoryRequest request = new CategoryRequest(
                1L,
                longName,
                "Test Description",
                null
        );

        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Měla by být zjištěna jedna chyba validace");

        ConstraintViolation<CategoryRequest> violation = violations.iterator().next();
        assertEquals("name", violation.getPropertyPath().toString());
        assertEquals("Jméno kategorie nesmí být delší než 100 znaků.", violation.getMessage());
    }

    @Test
    void testNameMaxLength() {
        String maxLengthName = "a".repeat(100);
        CategoryRequest request = new CategoryRequest(
                1L,
                maxLengthName,
                "Test Description",
                null
        );

        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Název s přesně 100 znaky by měl být validní");
    }

    @Test
    void testDescriptionTooLong() {
        String longDescription = "a".repeat(501);
        CategoryRequest request = new CategoryRequest(
                1L,
                "Test Category",
                longDescription,
                null
        );

        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Měla by být zjištěna jedna chyba validace");

        ConstraintViolation<CategoryRequest> violation = violations.iterator().next();
        assertEquals("description", violation.getPropertyPath().toString());
        assertEquals("Popis kategorie nesmí být delší než 500 znaků.", violation.getMessage());
    }

    @Test
    void testDescriptionMaxLength() {
        String maxLengthDescription = "a".repeat(500);
        CategoryRequest request = new CategoryRequest(
                1L,
                "Test Category",
                maxLengthDescription,
                null
        );

        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Popis s přesně 500 znaky by měl být validní");
    }

    @Test
    void testEmptyDescription() {
        CategoryRequest request = new CategoryRequest(
                1L,
                "Test Category",
                "",
                null
        );

        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Prázdný popis by měl být validní");
    }

    @Test
    void testNullDescription() {
        CategoryRequest request = new CategoryRequest(
                1L,
                "Test Category",
                null,
                null
        );

        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Null popis by měl být validní");
    }

    @Test
    void testMultipleViolations() {
        String longName = "a".repeat(101);
        String longDescription = "a".repeat(501);
        CategoryRequest request = new CategoryRequest(
                1L,
                longName,
                longDescription,
                null
        );

        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(request);
        assertEquals(2, violations.size(), "Měly by být zjištěny dvě chyby validace");

        Set<String> messages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());

        assertTrue(messages.contains("Jméno kategorie nesmí být delší než 100 znaků."));
        assertTrue(messages.contains("Popis kategorie nesmí být delší než 500 znaků."));
    }

    @Test
    void testEquality() {
        CategoryRequest request1 = new CategoryRequest(1L, "Test", "Description", 2L);
        CategoryRequest request2 = new CategoryRequest(1L, "Test", "Description", 2L);
        CategoryRequest request3 = new CategoryRequest(2L, "Test", "Description", 2L);

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1, request3);
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    void testToString() {
        CategoryRequest request = new CategoryRequest(1L, "Test", "Description", 2L);
        String toString = request.toString();

        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("name=Test"));
        assertTrue(toString.contains("description=Description"));
        assertTrue(toString.contains("parentId=2"));
    }
}