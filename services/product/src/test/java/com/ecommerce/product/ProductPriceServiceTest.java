package com.ecommerce.product;

import com.ecommerce.variant.Variant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductPriceServiceTest {

    private ProductPriceService productPriceService;
    private Product testProduct;
    private Variant variant1;
    private Variant variant2;
    private Variant variant3;

    @BeforeEach
    void setUp() {
        productPriceService = new ProductPriceService();

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");

        variant1 = new Variant();
        variant1.setId(1L);
        variant1.setDiscountedPrice(new BigDecimal("100.00"));
        variant1.setBasePrice(new BigDecimal("120.00"));
        variant1.setProduct(testProduct);

        variant2 = new Variant();
        variant2.setId(2L);
        variant2.setDiscountedPrice(new BigDecimal("150.00"));
        variant2.setBasePrice(new BigDecimal("170.00"));
        variant2.setProduct(testProduct);

        variant3 = new Variant();
        variant3.setId(3L);
        variant3.setDiscountedPrice(new BigDecimal("200.00"));
        variant3.setBasePrice(new BigDecimal("220.00"));
        variant3.setProduct(testProduct);
    }

    @Test
    void getCheapestVariantPrice_WithMultipleVariants_ReturnsCheapestPrice() {
        // Arrange
        testProduct.setVariants(List.of(variant1, variant2, variant3));

        // Act
        BigDecimal result = productPriceService.getCheapestVariantPrice(testProduct);

        // Assert
        assertEquals(new BigDecimal("100.00"), result);
    }

    @Test
    void getCheapestVariantPrice_WithSingleVariant_ReturnsItsPrice() {
        // Arrange
        testProduct.setVariants(List.of(variant2));

        // Act
        BigDecimal result = productPriceService.getCheapestVariantPrice(testProduct);

        // Assert
        assertEquals(new BigDecimal("150.00"), result);
    }

    @Test
    void getCheapestVariantPrice_WithNoVariants_ReturnsNull() {
        // Arrange
        testProduct.setVariants(Collections.emptyList());

        // Act
        BigDecimal result = productPriceService.getCheapestVariantPrice(testProduct);

        // Assert
        assertNull(result);
    }

    @Test
    void getCheapestVariantPrice_WithListOfProducts_ReturnsCheapestPriceAmongAllProducts() {
        // Arrange
        testProduct.setVariants(List.of(variant2)); // Price 150.00

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Test Product 2");

        Variant product2Variant = new Variant();
        product2Variant.setId(4L);
        product2Variant.setDiscountedPrice(new BigDecimal("90.00"));
        product2Variant.setBasePrice(new BigDecimal("110.00"));
        product2Variant.setProduct(product2);

        product2.setVariants(List.of(product2Variant));

        List<Product> products = List.of(testProduct, product2);

        // Act
        BigDecimal result = productPriceService.getCheapestVariantPrice(products);

        // Assert
        assertEquals(new BigDecimal("90.00"), result);
    }

    @Test
    void getCheapestVariantPrice_WithEmptyProductList_ReturnsNull() {
        // Act
        BigDecimal result = productPriceService.getCheapestVariantPrice(Collections.emptyList());

        // Assert
        assertNull(result);
    }

    @Test
    void getCheapestVariantPrice_WithProductsHavingNoVariants_ReturnsNull() {
        // Arrange
        testProduct.setVariants(Collections.emptyList());

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Test Product 2");
        product2.setVariants(Collections.emptyList());

        List<Product> products = List.of(testProduct, product2);

        // Act
        BigDecimal result = productPriceService.getCheapestVariantPrice(products);

        // Assert
        assertNull(result);
    }

    @Test
    void getHighestVariantPrice_WithMultipleProducts_ReturnsHighestPrice() {
        // Arrange
        testProduct.setVariants(List.of(variant1, variant2)); // Prices: 100.00, 150.00

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Test Product 2");

        Variant product2Variant = new Variant();
        product2Variant.setId(4L);
        product2Variant.setDiscountedPrice(new BigDecimal("250.00"));
        product2Variant.setBasePrice(new BigDecimal("270.00"));
        product2Variant.setProduct(product2);

        product2.setVariants(List.of(product2Variant));

        List<Product> products = List.of(testProduct, product2);

        // Act
        BigDecimal result = productPriceService.getHighestVariantPrice(products);

        // Assert
        assertEquals(new BigDecimal("250.00"), result);
    }

    @Test
    void getHighestVariantPrice_WithEmptyProductList_ReturnsNull() {
        // Act
        BigDecimal result = productPriceService.getHighestVariantPrice(Collections.emptyList());

        // Assert
        assertNull(result);
    }

    @Test
    void getCheapestVariantBasePrice_WithMultipleVariants_ReturnsCheapestBasePrice() {
        // Arrange
        testProduct.setVariants(List.of(variant1, variant2, variant3));

        // Act
        BigDecimal result = productPriceService.getCheapestVariantBasePrice(testProduct);

        // Assert
        assertEquals(new BigDecimal("120.00"), result);
    }

    @Test
    void getCheapestVariantBasePrice_WithNoVariants_ReturnsNull() {
        // Arrange
        testProduct.setVariants(Collections.emptyList());

        // Act
        BigDecimal result = productPriceService.getCheapestVariantBasePrice(testProduct);

        // Assert
        assertNull(result);
    }

    @Test
    void isVariantsPricesEqual_WithAllPricesEqual_ReturnsTrue() {
        // Arrange
        Variant v1 = new Variant();
        v1.setId(1L);
        v1.setDiscountedPrice(new BigDecimal("100.00"));

        Variant v2 = new Variant();
        v2.setId(2L);
        v2.setDiscountedPrice(new BigDecimal("100.00"));

        testProduct.setVariants(List.of(v1, v2));

        // Act
        boolean result = productPriceService.isVariantsPricesEqual(testProduct);

        // Assert
        assertTrue(result);
    }

    @Test
    void isVariantsPricesEqual_WithDifferentPrices_ReturnsFalse() {
        // Arrange
        testProduct.setVariants(List.of(variant1, variant2)); // Prices: 100.00, 150.00

        // Act
        boolean result = productPriceService.isVariantsPricesEqual(testProduct);

        // Assert
        assertFalse(result);
    }

    @Test
    void isVariantsPricesEqual_WithSingleVariant_ReturnsTrue() {
        // Arrange
        testProduct.setVariants(List.of(variant1));

        // Act
        boolean result = productPriceService.isVariantsPricesEqual(testProduct);

        // Assert
        assertTrue(result);
    }

    @Test
    void isVariantsPricesEqual_WithNoVariants_ReturnsFalse() {
        // Arrange
        testProduct.setVariants(new ArrayList<>());

        // Act
        boolean result = productPriceService.isVariantsPricesEqual(testProduct);

        // Assert
        assertFalse(result);
    }
}