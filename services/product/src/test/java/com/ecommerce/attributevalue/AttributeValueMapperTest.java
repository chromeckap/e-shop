package com.ecommerce.attributevalue;

import com.ecommerce.attribute.Attribute;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AttributeValueMapperTest {

    private AttributeValueMapper attributeValueMapper;
    private Attribute testAttribute;
    private AttributeValue testAttributeValue;
    private AttributeValueRequest testAttributeValueRequest;

    @BeforeEach
    void setUp() {
        attributeValueMapper = new AttributeValueMapper();

        testAttribute = Attribute.builder()
                .id(1L)
                .name("Color")
                .build();

        testAttributeValue = new AttributeValue();
        testAttributeValue.setId(1L);
        testAttributeValue.setValue("Red");
        testAttributeValue.setAttribute(testAttribute);

        testAttributeValueRequest = new AttributeValueRequest(1L, "Red");
    }

    @Test
    void toAttributeValue_WithValidRequest_MapsCorrectly() {
        // Act
        AttributeValue result = attributeValueMapper.toAttributeValue(testAttributeValueRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testAttributeValueRequest.value(), result.getValue());
        assertNull(result.getId()); // ID should not be set by the mapper
        assertNull(result.getAttribute()); // Attribute should not be set by the mapper
    }

    @Test
    void toAttributeValue_WithNullRequest_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> attributeValueMapper.toAttributeValue(null));
    }

    @Test
    void toResponse_WithValidAttributeValue_MapsCorrectly() {
        // Act
        AttributeValueResponse result = attributeValueMapper.toResponse(testAttributeValue);

        // Assert
        assertNotNull(result);
        assertEquals(testAttributeValue.getId(), result.id());
        assertEquals(testAttributeValue.getValue(), result.value());
        assertEquals(testAttribute.getId(), result.attributeId());
        assertEquals(testAttribute.getName(), result.attributeName());
    }

    @Test
    void toResponse_WithNullAttributeValue_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> attributeValueMapper.toResponse(null));
    }

    @Test
    void toResponse_WithNullAttribute_ThrowsNullPointerException() {
        // Arrange
        testAttributeValue.setAttribute(null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> attributeValueMapper.toResponse(testAttributeValue));
    }

    @Test
    void toResponse_WithAttributeHavingNullProperties_HandlesGracefully() {
        // Arrange
        Attribute attributeWithNulls = new Attribute();
        attributeWithNulls.setId(null);
        attributeWithNulls.setName(null);

        testAttributeValue.setAttribute(attributeWithNulls);

        // Act
        AttributeValueResponse result = attributeValueMapper.toResponse(testAttributeValue);

        // Assert
        assertNotNull(result);
        assertEquals(testAttributeValue.getId(), result.id());
        assertEquals(testAttributeValue.getValue(), result.value());
        assertNull(result.attributeId());
        assertNull(result.attributeName());
    }
}