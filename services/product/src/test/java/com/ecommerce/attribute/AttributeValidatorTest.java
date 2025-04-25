package com.ecommerce.attribute;

import com.ecommerce.exception.AttributeNotFoundException;
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
class AttributeValidatorTest {

    @Mock
    private AttributeRepository attributeRepository;

    @InjectMocks
    private AttributeValidator attributeValidator;

    private Set<Long> validAttributeIds;

    @BeforeEach
    void setUp() {
        validAttributeIds = Set.of(1L, 2L, 3L);
    }

    @Test
    void validateAllAttributesExist_WithAllIdsExisting_NoExceptionThrown() {
        // Arrange
        when(attributeRepository.countByIds(validAttributeIds)).thenReturn(validAttributeIds.size());

        // Act & Assert
        assertDoesNotThrow(() -> attributeValidator.validateAllAttributesExist(validAttributeIds));
        verify(attributeRepository).countByIds(validAttributeIds);
    }

    @Test
    void validateAllAttributesExist_WithMissingIds_ThrowsAttributeNotFoundException() {
        // Arrange
        when(attributeRepository.countByIds(validAttributeIds)).thenReturn(2); // One ID is missing

        // Act & Assert
        assertThrows(AttributeNotFoundException.class,
                () -> attributeValidator.validateAllAttributesExist(validAttributeIds));
        verify(attributeRepository).countByIds(validAttributeIds);
    }

    @Test
    void validateAllAttributesExist_WithEmptySet_NoExceptionThrown() {
        // Act
        attributeValidator.validateAllAttributesExist(Collections.emptySet());

        // Assert
        verify(attributeRepository, never()).countByIds(any());
    }

    @Test
    void validateAllAttributesExist_WithNullSet_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> attributeValidator.validateAllAttributesExist(null));
        verify(attributeRepository, never()).countByIds(any());
    }
}