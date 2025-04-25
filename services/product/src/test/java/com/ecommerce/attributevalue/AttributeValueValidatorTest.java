package com.ecommerce.attributevalue;

import com.ecommerce.exception.AttributeValueNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttributeValueValidatorTest {

    @Mock
    private AttributeValueRepository attributeValueRepository;

    @InjectMocks
    private AttributeValueValidator attributeValueValidator;

    private Set<Long> validAttributeValueIds;

    @BeforeEach
    void setUp() {
        validAttributeValueIds = Set.of(1L, 2L, 3L);
    }

    @Test
    void validateAllAttributeValuesExist_WithAllIdsExisting_NoExceptionThrown() {
        // Arrange
        when(attributeValueRepository.countByIds(validAttributeValueIds)).thenReturn(validAttributeValueIds.size());

        // Act & Assert
        assertDoesNotThrow(() -> attributeValueValidator.validateAllAttributeValuesExist(validAttributeValueIds));
        verify(attributeValueRepository).countByIds(validAttributeValueIds);
    }

    @Test
    void validateAllAttributeValuesExist_WithMissingIds_ThrowsAttributeValueNotFoundException() {
        // Arrange
        when(attributeValueRepository.countByIds(validAttributeValueIds)).thenReturn(2); // One ID is missing

        // Act & Assert
        assertThrows(AttributeValueNotFoundException.class,
                () -> attributeValueValidator.validateAllAttributeValuesExist(validAttributeValueIds));
        verify(attributeValueRepository).countByIds(validAttributeValueIds);
    }

    @Test
    void validateAllAttributeValuesExist_WithEmptySet_NoExceptionThrown() {
        // Act
        attributeValueValidator.validateAllAttributeValuesExist(Collections.emptySet());

        // Assert
        verify(attributeValueRepository, never()).countByIds(null);
    }

    @Test
    void validateAllAttributeValuesExist_WithNullSet_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> attributeValueValidator.validateAllAttributeValuesExist(null));
        verify(attributeValueRepository, never()).countByIds(Collections.emptySet());
    }
}