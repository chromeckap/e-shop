package com.ecommerce.variant;

import com.ecommerce.attribute.Attribute;
import com.ecommerce.attributevalue.AttributeValue;
import com.ecommerce.exception.QuantityOutOfStockException;
import com.ecommerce.exception.VariantNotFoundException;
import com.ecommerce.product.Product;
import com.ecommerce.variant.purchase.PurchaseRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VariantValidatorTest {

    @Mock
    private VariantRepository variantRepository;

    @InjectMocks
    private VariantValidator variantValidator;

    private Set<Long> validVariantIds;
    private Variant testVariant;
    private Product testProduct;
    private PurchaseRequest testPurchaseRequest;

    @BeforeEach
    void setUp() {
        validVariantIds = Set.of(1L, 2L, 3L);

        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .build();

        testVariant = Variant.builder()
                .id(1L)
                .sku("TEST-SKU")
                .basePrice(new BigDecimal("100.00"))
                .quantity(10)
                .quantityUnlimited(false)
                .product(testProduct)
                .build();

        testPurchaseRequest = new PurchaseRequest(1L, 5); // Requesting 5 of 10 available
    }

    @Test
    void validateAllVariantsExist_WithAllIdsExisting_NoExceptionThrown() {
        // Arrange
        when(variantRepository.countByIds(validVariantIds)).thenReturn(validVariantIds.size());

        // Act & Assert
        assertDoesNotThrow(() -> variantValidator.validateAllVariantsExist(validVariantIds));
        verify(variantRepository).countByIds(validVariantIds);
    }

    @Test
    void validateAllVariantsExist_WithMissingIds_ThrowsVariantNotFoundException() {
        // Arrange
        when(variantRepository.countByIds(validVariantIds)).thenReturn(2); // One ID is missing

        // Act & Assert
        assertThrows(VariantNotFoundException.class,
                () -> variantValidator.validateAllVariantsExist(validVariantIds));
        verify(variantRepository).countByIds(validVariantIds);
    }

    @Test
    void validateAllVariantsExist_WithEmptySet_NoExceptionThrown() {
        // Act
        variantValidator.validateAllVariantsExist(Collections.emptySet());

        // Assert
        verify(variantRepository, never()).countByIds(null);
    }

    @Test
    void validateProductNumberVariants_WhenUnderLimit_NoExceptionThrown() {
        // Arrange
        // Create a product with one attribute having 3 values
        Attribute attribute = Attribute.builder()
                .id(1L)
                .name("Color")
                .build();

        AttributeValue attributeValue1 = AttributeValue.builder().attribute(attribute).build();
        AttributeValue attributeValue2 = AttributeValue.builder().attribute(attribute).build();
        AttributeValue attributeValue3 = AttributeValue.builder().attribute(attribute).build();


        attribute.setValues(Arrays.asList(attributeValue1, attributeValue2, attributeValue3));

        List<Attribute> attributes = new ArrayList<>();
        attributes.add(attribute);

        // Currently has 2 variants, max should be 3 based on attribute values
        testProduct.setVariants(Arrays.asList(
                Variant.builder().id(1L).build(),
                Variant.builder().id(2L).build()
        ));
        testProduct.setAttributes(new HashSet<>(attributes));

        // Act & Assert
        assertDoesNotThrow(() -> variantValidator.validateProductNumberVariants(testVariant));
    }

    @Test
    void validateProductNumberVariants_WhenAtLimit_ThrowsIllegalArgumentException() {
        // Arrange
        // Create a product with one attribute having 2 values
        Attribute attribute = Attribute.builder()
                .id(1L)
                .name("Color")
                .build();

        attribute.setValues(Arrays.asList(
                createAttributeValue(1L, "Red", attribute),
                createAttributeValue(2L, "Blue", attribute)
        ));

        // Already has 2 variants, max is 2 based on attribute values
        testProduct.setVariants(Arrays.asList(
                Variant.builder().id(1L).build(),
                Variant.builder().id(2L).build()
        ));
        testProduct.setAttributes(Set.of(attribute));

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> variantValidator.validateProductNumberVariants(testVariant));
    }

    @Test
    void validateProductNumberVariants_WithNoAttributes_LimitIsOne() {
        // Arrange
        // Product with no attributes should have a max of 1 variant
        testProduct.setAttributes(Collections.emptySet());

        // Already has 1 variant
        testProduct.setVariants(List.of(Variant.builder().id(1L).build()));

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> variantValidator.validateProductNumberVariants(testVariant));
    }

    @Test
    void validateProductNumberVariants_WithMultipleAttributes_CalculatesCorrectLimit() {
        // Arrange
        // Create a product with two attributes: Color (3 values) and Size (2 values)
        Attribute colorAttribute = Attribute.builder()
                .id(1L)
                .name("Color")
                .build();

        colorAttribute.setValues(Arrays.asList(
                createAttributeValue(1L, "Red", colorAttribute),
                createAttributeValue(2L, "Blue", colorAttribute),
                createAttributeValue(3L, "Green", colorAttribute)
        ));

        Attribute sizeAttribute = Attribute.builder()
                .id(2L)
                .name("Size")
                .build();

        sizeAttribute.setValues(Arrays.asList(
                createAttributeValue(4L, "Small", sizeAttribute),
                createAttributeValue(5L, "Large", sizeAttribute)
        ));

        // Max variants should be 3 * 2 = 6
        // Currently has 5 variants
        testProduct.setVariants(Arrays.asList(
                Variant.builder().id(1L).build(),
                Variant.builder().id(2L).build(),
                Variant.builder().id(3L).build(),
                Variant.builder().id(4L).build(),
                Variant.builder().id(5L).build()
        ));
        testProduct.setAttributes(Set.of(colorAttribute, sizeAttribute));

        // Act & Assert
        assertDoesNotThrow(() -> variantValidator.validateProductNumberVariants(testVariant));
    }

    @Test
    void validateAvailableQuantity_WithSufficientQuantity_NoExceptionThrown() {
        // Arrange - testVariant has 10, requesting 5

        // Act & Assert
        assertDoesNotThrow(() -> variantValidator.validateAvailableQuantity(testVariant, testPurchaseRequest));
    }

    @Test
    void validateAvailableQuantity_WithExactQuantity_NoExceptionThrown() {
        // Arrange
        PurchaseRequest exactRequest = new PurchaseRequest(1L, 10); // Exactly what's available

        // Act & Assert
        assertDoesNotThrow(() -> variantValidator.validateAvailableQuantity(testVariant, exactRequest));
    }

    @Test
    void validateAvailableQuantity_WithInsufficientQuantity_ThrowsQuantityOutOfStockException() {
        // Arrange
        PurchaseRequest excessRequest = new PurchaseRequest(1L, 15); // More than available

        // Act & Assert
        assertThrows(QuantityOutOfStockException.class,
                () -> variantValidator.validateAvailableQuantity(testVariant, excessRequest));
    }

    @Test
    void validateAvailableQuantity_WithUnlimitedQuantity_NoExceptionThrown() {
        // Arrange
        testVariant.setQuantityUnlimited(true);
        testVariant.setQuantity(0);
        PurchaseRequest largeRequest = new PurchaseRequest(1L, 5); // Should be fine with unlimited

        // Act & Assert
        assertDoesNotThrow(() -> variantValidator.validateAvailableQuantity(testVariant, largeRequest));
    }

    @Test
    void validateSetNotEmpty_WithValidSet_NoExceptionThrown() {
        // Arrange
        Set<PurchaseRequest> requests = Set.of(
                new PurchaseRequest(1L, 5),
                new PurchaseRequest(2L, 3)
        );

        // Act & Assert
        assertDoesNotThrow(() -> variantValidator.validateSetNotEmpty(requests));
    }

    @Test
    void validateSetNotEmpty_WithEmptySet_ThrowsIllegalArgumentException() {
        // Arrange
        Set<PurchaseRequest> emptySet = Collections.emptySet();

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> variantValidator.validateSetNotEmpty(emptySet));
    }

    @Test
    void validateSetNotEmpty_WithNullSet_ThrowsIllegalArgumentException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> variantValidator.validateSetNotEmpty(null));
    }

    @Test
    void validateTotalPriceIsNotOver_WithPriceUnderLimit_NoExceptionThrown() {
        // Arrange
        List<Variant> variants = List.of(
                createVariant(1L, new BigDecimal("50.00")),
                createVariant(2L, new BigDecimal("30.00"))
        );

        Map<Long, PurchaseRequest> requests = Map.of(
                1L, new PurchaseRequest(1L, 1), // 50.00
                2L, new PurchaseRequest(2L, 1)  // 30.00
        );

        BigDecimal maxPrice = new BigDecimal("100.00"); // Total is 80.00

        // Act & Assert
        assertDoesNotThrow(() ->
                variantValidator.validateTotalPriceIsNotOver(variants, requests, maxPrice));
    }

    @Test
    void validateTotalPriceIsNotOver_WithPriceExactlyAtLimit_NoExceptionThrown() {
        // Arrange
        List<Variant> variants = List.of(
                createVariant(1L, new BigDecimal("50.00")),
                createVariant(2L, new BigDecimal("50.00"))
        );

        Map<Long, PurchaseRequest> requests = Map.of(
                1L, new PurchaseRequest(1L, 1), // 50.00
                2L, new PurchaseRequest(2L, 1)  // 50.00
        );

        BigDecimal maxPrice = new BigDecimal("100.00"); // Total is exactly 100.00

        // Act & Assert
        assertDoesNotThrow(() ->
                variantValidator.validateTotalPriceIsNotOver(variants, requests, maxPrice));
    }

    @Test
    void validateTotalPriceIsNotOver_WithPriceOverLimit_ThrowsIllegalArgumentException() {
        // Arrange
        List<Variant> variants = List.of(
                createVariant(1L, new BigDecimal("60.00")),
                createVariant(2L, new BigDecimal("50.00"))
        );

        Map<Long, PurchaseRequest> requests = Map.of(
                1L, new PurchaseRequest(1L, 1), // 60.00
                2L, new PurchaseRequest(2L, 1)  // 50.00
        );

        BigDecimal maxPrice = new BigDecimal("100.00"); // Total is 110.00

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                variantValidator.validateTotalPriceIsNotOver(variants, requests, maxPrice));
    }

    @Test
    void validateTotalPriceIsNotOver_WithVariantNotInRequests_IgnoresVariant() {
        // Arrange
        List<Variant> variants = List.of(
                createVariant(1L, new BigDecimal("50.00")),
                createVariant(2L, new BigDecimal("60.00")) // This one is not in the requests
        );

        Map<Long, PurchaseRequest> requests = Map.of(
                1L, new PurchaseRequest(1L, 1) // Only requesting the first variant
        );

        BigDecimal maxPrice = new BigDecimal("50.00"); // Total should be exactly 50.00

        // Act & Assert
        assertDoesNotThrow(() ->
                variantValidator.validateTotalPriceIsNotOver(variants, requests, maxPrice));
    }

    @Test
    void validateTotalPriceIsNotOver_WithMultipleQuantities_CalculatesCorrectly() {
        // Arrange
        List<Variant> variants = List.of(
                createVariant(1L, new BigDecimal("20.00")),
                createVariant(2L, new BigDecimal("30.00"))
        );

        Map<Long, PurchaseRequest> requests = Map.of(
                1L, new PurchaseRequest(1L, 2), // 2 * 20.00 = 40.00
                2L, new PurchaseRequest(2L, 1)  // 1 * 30.00 = 30.00
        );

        BigDecimal maxPrice = new BigDecimal("70.00"); // Total is exactly 70.00

        // Act & Assert
        assertDoesNotThrow(() ->
                variantValidator.validateTotalPriceIsNotOver(variants, requests, maxPrice));
    }

    // Helper methods
    private AttributeValue createAttributeValue(Long id, String value, Attribute attribute) {
        AttributeValue attributeValue = new AttributeValue();
        attributeValue.setId(id);
        attributeValue.setValue(value);
        attributeValue.setAttribute(attribute);
        return attributeValue;
    }

    private Variant createVariant(Long id, BigDecimal price) {
        return Variant.builder()
                .id(id)
                .basePrice(price)
                .discountedPrice(null) // Use base price
                .build();
    }
}