package com.ecommerce.product;

import com.ecommerce.exception.ProductNotFoundException;
import com.ecommerce.exception.SelfRelatingProductException;
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
class ProductValidatorTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductValidator productValidator;

    private Set<Long> validRelatedProductIds;
    private Long testProductId;

    @BeforeEach
    void setUp() {
        testProductId = 1L;
        validRelatedProductIds = Set.of(2L, 3L, 4L);
    }

    @Test
    void validateNoSelfReference_WithNonSelfReference_NoExceptionThrown() {
        // Arrange - using validRelatedProductIds that does not contain testProductId

        // Act & Assert
        assertDoesNotThrow(() -> productValidator.validateNoSelfReference(validRelatedProductIds, testProductId));
    }

    @Test
    void validateNoSelfReference_WithSelfReference_ThrowsSelfRelatingProductException() {
        // Arrange
        Set<Long> selfReferencingIds = Set.of(1L, 2L, 3L); // Contains 1L which is the testProductId

        // Act & Assert
        assertThrows(SelfRelatingProductException.class,
                () -> productValidator.validateNoSelfReference(selfReferencingIds, testProductId));
    }

    @Test
    void validateNoSelfReference_WithEmptyRelatedProductIds_NoExceptionThrown() {
        // Arrange
        Set<Long> emptySet = Collections.emptySet();

        // Act & Assert
        assertDoesNotThrow(() -> productValidator.validateNoSelfReference(emptySet, testProductId));
    }

    @Test
    void validateAllProductsExist_WithAllIdsExisting_NoExceptionThrown() {
        // Arrange
        when(productRepository.countByIds(validRelatedProductIds)).thenReturn(validRelatedProductIds.size());

        // Act & Assert
        assertDoesNotThrow(() -> productValidator.validateAllProductsExist(validRelatedProductIds));
        verify(productRepository).countByIds(validRelatedProductIds);
    }

    @Test
    void validateAllProductsExist_WithMissingIds_ThrowsProductNotFoundException() {
        // Arrange
        when(productRepository.countByIds(validRelatedProductIds)).thenReturn(2); // One ID is missing

        // Act & Assert
        assertThrows(ProductNotFoundException.class,
                () -> productValidator.validateAllProductsExist(validRelatedProductIds));
        verify(productRepository).countByIds(validRelatedProductIds);
    }

    @Test
    void validateAllProductsExist_WithEmptySet_NoExceptionThrown() {
        // Act
        productValidator.validateAllProductsExist(Collections.emptySet());

        // Assert
        verify(productRepository, never()).countByIds(any());
    }

    @Test
    void validateAllProductsExist_WithNullSet_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> productValidator.validateAllProductsExist(null));
        verify(productRepository, never()).countByIds(any());
    }
}