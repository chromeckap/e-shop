package com.ecommerce.attributevalue;

import com.ecommerce.attribute.Attribute;
import com.ecommerce.variant.Variant;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AttributeValueTest {

    @Test
    void constructor_EmptyConstructor_CreatesInstance() {
        // Act
        AttributeValue attributeValue = new AttributeValue();

        // Assert
        assertNotNull(attributeValue);
        assertNull(attributeValue.getId());
        assertNull(attributeValue.getValue());
        assertNull(attributeValue.getAttribute());
        assertNotNull(attributeValue.getVariants());
        assertTrue(attributeValue.getVariants().isEmpty());
    }

    @Test
    void constructor_AllArgsConstructor_CreatesInstance() {
        // Arrange
        Long id = 1L;
        String value = "Red";
        Attribute attribute = new Attribute();
        List<Variant> variants = new ArrayList<>();

        // Act
        AttributeValue attributeValue = new AttributeValue(id, value, attribute, variants);

        // Assert
        assertNotNull(attributeValue);
        assertEquals(id, attributeValue.getId());
        assertEquals(value, attributeValue.getValue());
        assertEquals(attribute, attributeValue.getAttribute());
        assertEquals(variants, attributeValue.getVariants());
    }

    @Test
    void builder_WithAllProperties_CreatesInstance() {
        // Arrange
        Long id = 1L;
        String value = "Red";
        Attribute attribute = new Attribute();
        List<Variant> variants = new ArrayList<>();

        // Act
        AttributeValue attributeValue = AttributeValue.builder()
                .id(id)
                .value(value)
                .attribute(attribute)
                .variants(variants)
                .build();

        // Assert
        assertNotNull(attributeValue);
        assertEquals(id, attributeValue.getId());
        assertEquals(value, attributeValue.getValue());
        assertEquals(attribute, attributeValue.getAttribute());
        assertEquals(variants, attributeValue.getVariants());
    }

    @Test
    void builder_WithNoProperties_CreatesInstanceWithDefaultValues() {
        // Act
        AttributeValue attributeValue = AttributeValue.builder().build();

        // Assert
        assertNotNull(attributeValue);
        assertNull(attributeValue.getId());
        assertNull(attributeValue.getValue());
        assertNull(attributeValue.getAttribute());
        assertNotNull(attributeValue.getVariants());
        assertTrue(attributeValue.getVariants().isEmpty());
    }

    @Test
    void builder_WithOnlyIdAndValue_CreatesInstanceWithDefaultCollections() {
        // Arrange
        Long id = 1L;
        String value = "Red";

        // Act
        AttributeValue attributeValue = AttributeValue.builder()
                .id(id)
                .value(value)
                .build();

        // Assert
        assertNotNull(attributeValue);
        assertEquals(id, attributeValue.getId());
        assertEquals(value, attributeValue.getValue());
        assertNull(attributeValue.getAttribute());
        assertNotNull(attributeValue.getVariants());
        assertTrue(attributeValue.getVariants().isEmpty());
    }

    @Test
    void settersAndGetters_WorkCorrectly() {
        // Arrange
        AttributeValue attributeValue = new AttributeValue();
        Long id = 1L;
        String value = "Red";
        Attribute attribute = new Attribute();
        List<Variant> variants = new ArrayList<>();

        // Act
        attributeValue.setId(id);
        attributeValue.setValue(value);
        attributeValue.setAttribute(attribute);
        attributeValue.setVariants(variants);

        // Assert
        assertEquals(id, attributeValue.getId());
        assertEquals(value, attributeValue.getValue());
        assertEquals(attribute, attributeValue.getAttribute());
        assertEquals(variants, attributeValue.getVariants());
    }

    @Test
    void equals_WithSameInstance_ReturnsTrue() {
        // Arrange
        AttributeValue attributeValue = AttributeValue.builder()
                .id(1L)
                .value("Red")
                .build();

        // Act & Assert
        assertEquals(attributeValue, attributeValue);
    }

    @Test
    void equals_WithEqualId_ReturnsTrue() {
        // Arrange
        AttributeValue attributeValue1 = AttributeValue.builder()
                .id(1L)
                .value("Red")
                .build();

        AttributeValue attributeValue2 = AttributeValue.builder()
                .id(1L)
                .value("Blue") // Different value, but same ID
                .build();

        // Act & Assert
        assertEquals(attributeValue1, attributeValue2);
        assertEquals(attributeValue1.hashCode(), attributeValue2.hashCode());
    }

    @Test
    void equals_WithDifferentId_ReturnsFalse() {
        // Arrange
        AttributeValue attributeValue1 = AttributeValue.builder()
                .id(1L)
                .value("Red")
                .build();

        AttributeValue attributeValue2 = AttributeValue.builder()
                .id(2L)
                .value("Red") // Same value, but different ID
                .build();

        // Act & Assert
        assertNotEquals(attributeValue1, attributeValue2);
    }

    @Test
    void equals_WithOneNullId_ReturnsFalse() {
        // Arrange
        AttributeValue attributeValue1 = AttributeValue.builder()
                .id(1L)
                .value("Red")
                .build();

        AttributeValue attributeValue2 = AttributeValue.builder()
                .id(null)
                .value("Red")
                .build();

        // Act & Assert
        assertNotEquals(attributeValue1, attributeValue2);
        assertNotEquals(attributeValue2, attributeValue1);
    }

    @Test
    void equals_WithDifferentType_ReturnsFalse() {
        // Arrange
        AttributeValue attributeValue = AttributeValue.builder()
                .id(1L)
                .value("Red")
                .build();

        // Act & Assert
        assertNotEquals(attributeValue, "Not an attribute value");
    }

    @Test
    void equals_WithNull_ReturnsFalse() {
        // Arrange
        AttributeValue attributeValue = AttributeValue.builder()
                .id(1L)
                .value("Red")
                .build();

        // Act & Assert
        assertNotEquals(attributeValue, null);
    }

    @Test
    void toString_ContainsAllFields() {
        // Arrange
        AttributeValue attributeValue = AttributeValue.builder()
                .id(1L)
                .value("Red")
                .build();

        // Act
        String toString = attributeValue.toString();

        // Assert
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("value=Red"));
        assertTrue(toString.contains("attribute=null"));
        assertTrue(toString.contains("variants=[]"));
    }

    @Test
    void addVariant_AddsVariantCorrectly() {
        // Arrange
        AttributeValue attributeValue = AttributeValue.builder()
                .id(1L)
                .value("Red")
                .build();

        Variant variant = new Variant();
        variant.setId(1L);

        // Act
        // Manually adding the variant to test the relationship
        attributeValue.getVariants().add(variant);

        // Assert
        assertEquals(1, attributeValue.getVariants().size());
        assertEquals(variant, attributeValue.getVariants().getFirst());
    }

    @Test
    void setAttribute_SetsAttributeCorrectly() {
        // Arrange
        AttributeValue attributeValue = AttributeValue.builder()
                .id(1L)
                .value("Red")
                .build();

        Attribute attribute = Attribute.builder()
                .id(1L)
                .name("Color")
                .build();

        // Act
        attributeValue.setAttribute(attribute);

        // Assert
        assertEquals(attribute, attributeValue.getAttribute());
    }
}