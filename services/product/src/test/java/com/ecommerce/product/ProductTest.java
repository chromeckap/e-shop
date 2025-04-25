package com.ecommerce.product;

import com.ecommerce.attribute.Attribute;
import com.ecommerce.productimage.ProductImage;
import com.ecommerce.variant.Variant;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void constructor_EmptyConstructor_CreatesInstance() {
        // Act
        Product product = new Product();

        // Assert
        assertNotNull(product);
        assertNull(product.getId());
        assertNull(product.getName());
        assertNull(product.getDescription());
        assertFalse(product.isVisible());
        assertNotNull(product.getCategoryIds());
        assertTrue(product.getCategoryIds().isEmpty());
        assertNotNull(product.getImages());
        assertTrue(product.getImages().isEmpty());
        assertNotNull(product.getVariants());
        assertTrue(product.getVariants().isEmpty());
        assertNotNull(product.getRelatedProducts());
        assertTrue(product.getRelatedProducts().isEmpty());
        assertNotNull(product.getAttributes());
        assertTrue(product.getAttributes().isEmpty());
    }

    @Test
    void constructor_AllArgsConstructor_CreatesInstance() {
        // Arrange
        Long id = 1L;
        String name = "Test Product";
        String description = "Test Description";
        boolean isVisible = true;
        Set<Long> categoryIds = new HashSet<>();
        List<ProductImage> images = new ArrayList<>();
        List<Variant> variants = new ArrayList<>();
        Set<Product> relatedProducts = new HashSet<>();
        Set<Attribute> attributes = new HashSet<>();

        // Act
        Product product = new Product(id, name, description, isVisible, categoryIds, images, variants, relatedProducts, attributes);

        // Assert
        assertNotNull(product);
        assertEquals(id, product.getId());
        assertEquals(name, product.getName());
        assertEquals(description, product.getDescription());
        assertEquals(isVisible, product.isVisible());
        assertEquals(categoryIds, product.getCategoryIds());
        assertEquals(images, product.getImages());
        assertEquals(variants, product.getVariants());
        assertEquals(relatedProducts, product.getRelatedProducts());
        assertEquals(attributes, product.getAttributes());
    }

    @Test
    void builder_WithAllProperties_CreatesInstance() {
        // Arrange
        Long id = 1L;
        String name = "Test Product";
        String description = "Test Description";
        boolean isVisible = true;
        Set<Long> categoryIds = new HashSet<>();
        List<ProductImage> images = new ArrayList<>();
        List<Variant> variants = new ArrayList<>();
        Set<Product> relatedProducts = new HashSet<>();
        Set<Attribute> attributes = new HashSet<>();

        // Act
        Product product = Product.builder()
                .id(id)
                .name(name)
                .description(description)
                .isVisible(isVisible)
                .categoryIds(categoryIds)
                .images(images)
                .variants(variants)
                .relatedProducts(relatedProducts)
                .attributes(attributes)
                .build();

        // Assert
        assertNotNull(product);
        assertEquals(id, product.getId());
        assertEquals(name, product.getName());
        assertEquals(description, product.getDescription());
        assertEquals(isVisible, product.isVisible());
        assertEquals(categoryIds, product.getCategoryIds());
        assertEquals(images, product.getImages());
        assertEquals(variants, product.getVariants());
        assertEquals(relatedProducts, product.getRelatedProducts());
        assertEquals(attributes, product.getAttributes());
    }

    @Test
    void builder_WithNoProperties_CreatesInstanceWithDefaultCollections() {
        // Act
        Product product = Product.builder().build();

        // Assert
        assertNotNull(product);
        assertNull(product.getId());
        assertNull(product.getName());
        assertNull(product.getDescription());
        assertFalse(product.isVisible());
        assertNotNull(product.getCategoryIds());
        assertTrue(product.getCategoryIds().isEmpty());
        assertNotNull(product.getImages());
        assertTrue(product.getImages().isEmpty());
        assertNotNull(product.getVariants());
        assertTrue(product.getVariants().isEmpty());
        assertNotNull(product.getRelatedProducts());
        assertTrue(product.getRelatedProducts().isEmpty());
        assertNotNull(product.getAttributes());
        assertTrue(product.getAttributes().isEmpty());
    }

    @Test
    void settersAndGetters_WorkCorrectly() {
        // Arrange
        Product product = new Product();
        Long id = 1L;
        String name = "Test Product";
        String description = "Test Description";
        boolean isVisible = true;
        Set<Long> categoryIds = new HashSet<>();
        List<ProductImage> images = new ArrayList<>();
        List<Variant> variants = new ArrayList<>();
        Set<Product> relatedProducts = new HashSet<>();
        Set<Attribute> attributes = new HashSet<>();

        // Act
        product.setId(id);
        product.setName(name);
        product.setDescription(description);
        product.setVisible(isVisible);
        product.setCategoryIds(categoryIds);
        product.setImages(images);
        product.setVariants(variants);
        product.setRelatedProducts(relatedProducts);
        product.setAttributes(attributes);

        // Assert
        assertEquals(id, product.getId());
        assertEquals(name, product.getName());
        assertEquals(description, product.getDescription());
        assertEquals(isVisible, product.isVisible());
        assertEquals(categoryIds, product.getCategoryIds());
        assertEquals(images, product.getImages());
        assertEquals(variants, product.getVariants());
        assertEquals(relatedProducts, product.getRelatedProducts());
        assertEquals(attributes, product.getAttributes());
    }

    @Test
    void equals_WithSameInstance_ReturnsTrue() {
        // Arrange
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .build();

        // Act & Assert
        assertEquals(product, product);
    }

    @Test
    void equals_WithEqualId_ReturnsTrue() {
        // Arrange
        Product product1 = Product.builder()
                .id(1L)
                .name("Product 1")
                .build();

        Product product2 = Product.builder()
                .id(1L)
                .name("Product 2") // Different name, but same ID
                .build();

        // Act & Assert
        assertEquals(product1, product2);
        assertEquals(product1.hashCode(), product2.hashCode());
    }

    @Test
    void equals_WithDifferentId_ReturnsFalse() {
        // Arrange
        Product product1 = Product.builder()
                .id(1L)
                .name("Test Product")
                .build();

        Product product2 = Product.builder()
                .id(2L)
                .name("Test Product") // Same name, but different ID
                .build();

        // Act & Assert
        assertNotEquals(product1, product2);
    }

    @Test
    void equals_WithOneNullId_ReturnsFalse() {
        // Arrange
        Product product1 = Product.builder()
                .id(1L)
                .name("Test Product")
                .build();

        Product product2 = Product.builder()
                .id(null)
                .name("Test Product")
                .build();

        // Act & Assert
        assertNotEquals(product1, product2);
        assertNotEquals(product2, product1);
    }

    @Test
    void equals_WithDifferentType_ReturnsFalse() {
        // Arrange
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .build();

        // Act & Assert
        assertNotEquals(product, "Not a product");
    }

    @Test
    void equals_WithNull_ReturnsFalse() {
        // Arrange
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .build();

        // Act & Assert
        assertNotEquals(product, null);
    }

    @Test
    void toString_ContainsAllFields() {
        // Arrange
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .isVisible(true)
                .build();

        // Act
        String toString = product.toString();

        // Assert
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("name=Test Product"));
        assertTrue(toString.contains("description=Test Description"));
        assertTrue(toString.contains("isVisible=true"));
        assertTrue(toString.contains("categoryIds=[]"));
        assertTrue(toString.contains("images=[]"));
        assertTrue(toString.contains("variants=[]"));
    }

    @Test
    void addVariant_AddsVariantCorrectly() {
        // Arrange
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .build();

        Variant variant = new Variant();
        variant.setId(1L);
        variant.setProduct(product);

        // Act
        product.getVariants().add(variant);

        // Assert
        assertEquals(1, product.getVariants().size());
        assertEquals(variant, product.getVariants().getFirst());
        assertEquals(product, variant.getProduct());
    }

    @Test
    void addProductImage_AddsImageCorrectly() {
        // Arrange
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .build();

        ProductImage image = new ProductImage();
        image.setId(1L);
        image.setProduct(product);

        // Act
        product.getImages().add(image);

        // Assert
        assertEquals(1, product.getImages().size());
        assertEquals(image, product.getImages().getFirst());
        assertEquals(product, image.getProduct());
    }

    @Test
    void addRelatedProduct_AddsRelatedProductCorrectly() {
        // Arrange
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .build();

        Product relatedProduct = Product.builder()
                .id(2L)
                .name("Related Product")
                .build();

        // Act
        product.getRelatedProducts().add(relatedProduct);

        // Assert
        assertEquals(1, product.getRelatedProducts().size());
        assertTrue(product.getRelatedProducts().contains(relatedProduct));
    }

    @Test
    void addAttribute_AddsAttributeCorrectly() {
        // Arrange
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .build();

        Attribute attribute = Attribute.builder()
                .id(1L)
                .name("Color")
                .build();

        // Act
        product.getAttributes().add(attribute);

        // Assert
        assertEquals(1, product.getAttributes().size());
        assertTrue(product.getAttributes().contains(attribute));
    }

    @Test
    void addCategoryId_AddsCategoryIdCorrectly() {
        // Arrange
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .build();

        Long categoryId = 1L;

        // Act
        product.getCategoryIds().add(categoryId);

        // Assert
        assertEquals(1, product.getCategoryIds().size());
        assertTrue(product.getCategoryIds().contains(categoryId));
    }
}