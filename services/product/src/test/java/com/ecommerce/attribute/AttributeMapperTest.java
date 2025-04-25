package com.ecommerce.attribute;

import com.ecommerce.attributevalue.AttributeValue;
import com.ecommerce.attributevalue.AttributeValueMapper;
import com.ecommerce.attributevalue.AttributeValueRequest;
import com.ecommerce.attributevalue.AttributeValueResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttributeMapperTest {

    @Mock
    private AttributeValueMapper attributeValueMapper;

    @InjectMocks
    private AttributeMapper attributeMapper;

    private Attribute testAttribute;
    private AttributeRequest testAttributeRequest;
    private AttributeValue testAttributeValue;
    private AttributeValueResponse testAttributeValueResponse;

    @BeforeEach
    void setUp() {
        testAttributeRequest = new AttributeRequest(
                1L,
                "Color",
                List.of(new AttributeValueRequest(1L, "Red"))
        );

        testAttribute = Attribute.builder()
                .id(1L)
                .name("Color")
                .build();

        testAttributeValue = new AttributeValue();
        testAttributeValue.setId(1L);
        testAttributeValue.setValue("Red");
        testAttributeValue.setAttribute(testAttribute);

        testAttributeValueResponse = new AttributeValueResponse(1L, "Color", "Red", 1L);
    }

    @Test
    void toAttribute_WithValidRequest_MapsCorrectly() {
        // Act
        Attribute result = attributeMapper.toAttribute(testAttributeRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testAttributeRequest.id(), result.getId());
        assertEquals(testAttributeRequest.name(), result.getName());
        // Values should be empty as they are managed separately
        assertNotNull(result.getValues());
        assertTrue(result.getValues().isEmpty());
    }

    @Test
    void toAttribute_WithNullRequest_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> attributeMapper.toAttribute(null));
    }

    @Test
    void toResponse_WithAttributeHavingValues_MapsCorrectly() {
        // Arrange
        List<AttributeValue> values = new ArrayList<>();
        values.add(testAttributeValue);
        testAttribute.setValues(values);

        when(attributeValueMapper.toResponse(testAttributeValue)).thenReturn(testAttributeValueResponse);

        // Act
        AttributeResponse result = attributeMapper.toResponse(testAttribute);

        // Assert
        assertNotNull(result);
        assertEquals(testAttribute.getId(), result.id());
        assertEquals(testAttribute.getName(), result.name());
        assertNotNull(result.values());
        assertEquals(1, result.values().size());
        assertEquals(testAttributeValueResponse, result.values().getFirst());

        verify(attributeValueMapper).toResponse(testAttributeValue);
    }

    @Test
    void toResponse_WithAttributeHavingNoValues_MapsWithEmptyValuesList() {
        // Arrange
        testAttribute.setValues(Collections.emptyList());

        // Act
        AttributeResponse result = attributeMapper.toResponse(testAttribute);

        // Assert
        assertNotNull(result);
        assertEquals(testAttribute.getId(), result.id());
        assertEquals(testAttribute.getName(), result.name());
        assertNotNull(result.values());
        assertTrue(result.values().isEmpty());

        verify(attributeValueMapper, never()).toResponse(any());
    }

    @Test
    void toResponse_WithAttributeHavingNullValues_MapsWithEmptyValuesList() {
        // Arrange
        testAttribute.setValues(null);

        // Act
        AttributeResponse result = attributeMapper.toResponse(testAttribute);

        // Assert
        assertNotNull(result);
        assertEquals(testAttribute.getId(), result.id());
        assertEquals(testAttribute.getName(), result.name());
        assertNotNull(result.values());
        assertTrue(result.values().isEmpty());

        verify(attributeValueMapper, never()).toResponse(any());
    }

    @Test
    void toResponse_WithNullAttribute_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> attributeMapper.toResponse(null));
    }
}