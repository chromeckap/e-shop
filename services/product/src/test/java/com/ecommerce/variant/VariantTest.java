package com.ecommerce.variant;

import com.ecommerce.attributevalue.AttributeValue;
import com.ecommerce.product.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VariantTest {

    @Test
    void constructor_EmptyConstructor_CreatesInstance() {
        // Act
        Variant variant = new Variant();

        // Assert
        assertNotNull(variant);
        assertNull(variant.getId());
        assertNull(variant.getSku());
        assertNull(variant.getBasePrice());
        assertNull(variant.getDiscountedPrice());
        assertEquals(0, variant.getQuantity());
        assertFalse(variant.isQuantityUnlimited());
        assertNull(variant.getProduct());
        assertNotNull(variant.getValues());
        assertTrue(variant.getValues().isEmpty());
    }

    @Test
    void constructor_AllArgsConstructor_CreatesInstance() {
        // Arrange
        Long id = 1L;
        String sku = "TEST-SKU";
        BigDecimal basePrice = new BigDecimal("100.00");
        BigDecimal discountedPrice = new BigDecimal("80.00");
        int quantity = 10;
        boolean quantityUnlimited = false;
        Product product = Product.builder().id(1L).name("Test Product").build();
        List<AttributeValue> values = new ArrayList<>();

        // Act
        Variant variant = new Variant(id, sku, basePrice, discountedPrice, quantity, quantityUnlimited, product, values);

        // Assert
        assertNotNull(variant);
        assertEquals(id, variant.getId());
        assertEquals(sku, variant.getSku());
        assertEquals(basePrice, variant.getBasePrice());
        assertEquals(discountedPrice, variant.getDiscountedPrice());
        assertEquals(quantity, variant.getQuantity());
        assertEquals(quantityUnlimited, variant.isQuantityUnlimited());
        assertEquals(product, variant.getProduct());
        assertEquals(values, variant.getValues());
    }

    @Test
    void builder_WithAllProperties_CreatesInstance() {
        // Arrange
        Long id = 1L;
        String sku = "TEST-SKU";
        BigDecimal basePrice = new BigDecimal("100.00");
        BigDecimal discountedPrice = new BigDecimal("80.00");
        int quantity = 10;
        boolean quantityUnlimited = false;
        Product product = Product.builder().id(1L).name("Test Product").build();
        List<AttributeValue> values = new ArrayList<>();

        // Act
        Variant variant = Variant.builder()
                .id(id)
                .sku(sku)
                .basePrice(basePrice)
                .discountedPrice(discountedPrice)
                .quantity(quantity)
                .quantityUnlimited(quantityUnlimited)
                .product(product)
                .values(values)
                .build();

        // Assert
        assertNotNull(variant);
        assertEquals(id, variant.getId());
        assertEquals(sku, variant.getSku());
        assertEquals(basePrice, variant.getBasePrice());
        assertEquals(discountedPrice, variant.getDiscountedPrice());
        assertEquals(quantity, variant.getQuantity());
        assertEquals(quantityUnlimited, variant.isQuantityUnlimited());
        assertEquals(product, variant.getProduct());
        assertEquals(values, variant.getValues());
    }

    @Test
    void builder_WithNoProperties_CreatesInstanceWithDefaultValues() {
        // Act
        Variant variant = Variant.builder().build();

        // Assert
        assertNotNull(variant);
        assertNull(variant.getId());
        assertNull(variant.getSku());
        assertNull(variant.getBasePrice());
        assertNull(variant.getDiscountedPrice());
        assertEquals(0, variant.getQuantity());
        assertFalse(variant.isQuantityUnlimited());
        assertNull(variant.getProduct());
        assertNotNull(variant.getValues());
        assertTrue(variant.getValues().isEmpty());
    }

    @Test
    void settersAndGetters_WorkCorrectly() {
        // Arrange
        Variant variant = new Variant();
        Long id = 1L;
        String sku = "TEST-SKU";
        BigDecimal basePrice = new BigDecimal("100.00");
        BigDecimal discountedPrice = new BigDecimal("80.00");
        int quantity = 10;
        boolean quantityUnlimited = false;
        Product product = Product.builder().id(1L).name("Test Product").build();
        List<AttributeValue> values = new ArrayList<>();

        // Act
        variant.setId(id);
        variant.setSku(sku);
        variant.setBasePrice(basePrice);
        variant.setDiscountedPrice(discountedPrice);
        variant.setQuantity(quantity);
        variant.setQuantityUnlimited(quantityUnlimited);
        variant.setProduct(product);
        variant.setValues(values);

        // Assert
        assertEquals(id, variant.getId());
        assertEquals(sku, variant.getSku());
        assertEquals(basePrice, variant.getBasePrice());
        assertEquals(discountedPrice, variant.getDiscountedPrice());
        assertEquals(quantity, variant.getQuantity());
        assertEquals(quantityUnlimited, variant.isQuantityUnlimited());
        assertEquals(product, variant.getProduct());
        assertEquals(values, variant.getValues());
    }

    @Test
    void getPrice_WithDiscountedPrice_ReturnsDiscountedPrice() {
        // Arrange
        BigDecimal basePrice = new BigDecimal("100.00");
        BigDecimal discountedPrice = new BigDecimal("80.00");

        Variant variant = Variant.builder()
                .basePrice(basePrice)
                .discountedPrice(discountedPrice)
                .build();

        // Act
        BigDecimal result = variant.getPrice();

        // Assert
        assertEquals(discountedPrice, result);
    }

    @Test
    void getPrice_WithZeroDiscountedPrice_ReturnsBasePrice() {
        // Arrange
        BigDecimal basePrice = new BigDecimal("100.00");
        BigDecimal discountedPrice = BigDecimal.ZERO;

        Variant variant = Variant.builder()
                .basePrice(basePrice)
                .discountedPrice(discountedPrice)
                .build();

        // Act
        BigDecimal result = variant.getPrice();

        // Assert
        assertEquals(basePrice, result);
    }

    @Test
    void getPrice_WithNullDiscountedPrice_ReturnsBasePrice() {
        // Arrange
        BigDecimal basePrice = new BigDecimal("100.00");

        Variant variant = Variant.builder()
                .basePrice(basePrice)
                .discountedPrice(null)
                .build();

        // Act
        BigDecimal result = variant.getPrice();

        // Assert
        assertEquals(basePrice, result);
    }

    @Test
    void getPrice_WithNegativeDiscountedPrice_ReturnsBasePrice() {
        // Arrange
        BigDecimal basePrice = new BigDecimal("100.00");
        BigDecimal discountedPrice = new BigDecimal("-10.00");

        Variant variant = Variant.builder()
                .basePrice(basePrice)
                .discountedPrice(discountedPrice)
                .build();

        // Act
        BigDecimal result = variant.getPrice();

        // Assert
        assertEquals(basePrice, result);
    }

    @Test
    void equals_WithSameInstance_ReturnsTrue() {
        // Arrange
        Variant variant = Variant.builder()
                .id(1L)
                .sku("TEST-SKU")
                .build();

        // Act & Assert
        assertEquals(variant, variant);
    }

    @Test
    void equals_WithEqualId_ReturnsTrue() {
        // Arrange
        Variant variant1 = Variant.builder()
                .id(1L)
                .sku("TEST-SKU-1")
                .build();

        Variant variant2 = Variant.builder()
                .id(1L)
                .sku("TEST-SKU-2") // Different SKU, but same ID
                .build();

        // Act & Assert
        assertEquals(variant1, variant2);
        assertEquals(variant1.hashCode(), variant2.hashCode());
    }

    @Test
    void equals_WithDifferentId_ReturnsFalse() {
        // Arrange
        Variant variant1 = Variant.builder()
                .id(1L)
                .sku("TEST-SKU")
                .build();

        Variant variant2 = Variant.builder()
                .id(2L)
                .sku("TEST-SKU") // Same SKU, but different ID
                .build();

        // Act & Assert
        assertNotEquals(variant1, variant2);
    }

    @Test
    void equals_WithOneNullId_ReturnsFalse() {
        // Arrange
        Variant variant1 = Variant.builder()
                .id(1L)
                .sku("TEST-SKU")
                .build();

        Variant variant2 = Variant.builder()
                .id(null)
                .sku("TEST-SKU")
                .build();

        // Act & Assert
        assertNotEquals(variant1, variant2);
        assertNotEquals(variant2, variant1);
    }

    @Test
    void equals_WithDifferentType_ReturnsFalse() {
        // Arrange
        Variant variant = Variant.builder()
                .id(1L)
                .sku("TEST-SKU")
                .build();

        // Act & Assert
        assertNotEquals(variant, "Not a variant");
    }

    @Test
    void equals_WithNull_ReturnsFalse() {
        // Arrange
        Variant variant = Variant.builder()
                .id(1L)
                .sku("TEST-SKU")
                .build();

        // Act & Assert
        assertNotEquals(variant, null);
    }

    @Test
    void toString_ContainsAllFields() {
        // Arrange
        Variant variant = Variant.builder()
                .id(1L)
                .sku("TEST-SKU")
                .basePrice(new BigDecimal("100.00"))
                .discountedPrice(new BigDecimal("80.00"))
                .quantity(10)
                .quantityUnlimited(false)
                .build();

        // Act
        String toString = variant.toString();

        // Assert
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("sku=TEST-SKU"));
        assertTrue(toString.contains("basePrice=100.00"));
        assertTrue(toString.contains("discountedPrice=80.00"));
        assertTrue(toString.contains("quantity=10"));
        assertTrue(toString.contains("quantityUnlimited=false"));
    }
}