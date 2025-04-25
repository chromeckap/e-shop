package com.ecommerce.productimage;

import com.ecommerce.product.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductImageTest {

    @Test
    void constructor_EmptyConstructor_CreatesInstance() {
        // Act
        ProductImage productImage = new ProductImage();

        // Assert
        assertNotNull(productImage);
        assertNull(productImage.getId());
        assertNull(productImage.getImagePath());
        assertNull(productImage.getUploadOrder());
        assertNull(productImage.getProduct());
    }

    @Test
    void constructor_AllArgsConstructor_CreatesInstance() {
        // Arrange
        Long id = 1L;
        String imagePath = "test-image.jpg";
        Integer uploadOrder = 0;
        Product product = Product.builder().id(1L).name("Test Product").build();

        // Act
        ProductImage productImage = new ProductImage(id, imagePath, uploadOrder, product);

        // Assert
        assertNotNull(productImage);
        assertEquals(id, productImage.getId());
        assertEquals(imagePath, productImage.getImagePath());
        assertEquals(uploadOrder, productImage.getUploadOrder());
        assertEquals(product, productImage.getProduct());
    }

    @Test
    void builder_WithAllProperties_CreatesInstance() {
        // Arrange
        Long id = 1L;
        String imagePath = "test-image.jpg";
        Integer uploadOrder = 0;
        Product product = Product.builder().id(1L).name("Test Product").build();

        // Act
        ProductImage productImage = ProductImage.builder()
                .id(id)
                .imagePath(imagePath)
                .uploadOrder(uploadOrder)
                .product(product)
                .build();

        // Assert
        assertNotNull(productImage);
        assertEquals(id, productImage.getId());
        assertEquals(imagePath, productImage.getImagePath());
        assertEquals(uploadOrder, productImage.getUploadOrder());
        assertEquals(product, productImage.getProduct());
    }

    @Test
    void builder_WithNoProperties_CreatesInstanceWithNullFields() {
        // Act
        ProductImage productImage = ProductImage.builder().build();

        // Assert
        assertNotNull(productImage);
        assertNull(productImage.getId());
        assertNull(productImage.getImagePath());
        assertNull(productImage.getUploadOrder());
        assertNull(productImage.getProduct());
    }

    @Test
    void settersAndGetters_WorkCorrectly() {
        // Arrange
        ProductImage productImage = new ProductImage();
        Long id = 1L;
        String imagePath = "test-image.jpg";
        Integer uploadOrder = 0;
        Product product = Product.builder().id(1L).name("Test Product").build();

        // Act
        productImage.setId(id);
        productImage.setImagePath(imagePath);
        productImage.setUploadOrder(uploadOrder);
        productImage.setProduct(product);

        // Assert
        assertEquals(id, productImage.getId());
        assertEquals(imagePath, productImage.getImagePath());
        assertEquals(uploadOrder, productImage.getUploadOrder());
        assertEquals(product, productImage.getProduct());
    }

    @Test
    void equals_WithSameInstance_ReturnsTrue() {
        // Arrange
        ProductImage productImage = ProductImage.builder()
                .id(1L)
                .imagePath("test-image.jpg")
                .build();

        // Act & Assert
        assertEquals(productImage, productImage);
    }

    @Test
    void equals_WithEqualId_ReturnsTrue() {
        // Arrange
        ProductImage productImage1 = ProductImage.builder()
                .id(1L)
                .imagePath("image1.jpg")
                .build();

        ProductImage productImage2 = ProductImage.builder()
                .id(1L)
                .imagePath("image2.jpg") // Different path, but same ID
                .build();

        // Act & Assert
        assertEquals(productImage1, productImage2);
        assertEquals(productImage1.hashCode(), productImage2.hashCode());
    }

    @Test
    void equals_WithDifferentId_ReturnsFalse() {
        // Arrange
        ProductImage productImage1 = ProductImage.builder()
                .id(1L)
                .imagePath("test-image.jpg")
                .build();

        ProductImage productImage2 = ProductImage.builder()
                .id(2L)
                .imagePath("test-image.jpg") // Same path, but different ID
                .build();

        // Act & Assert
        assertNotEquals(productImage1, productImage2);
    }

    @Test
    void equals_WithOneNullId_ReturnsFalse() {
        // Arrange
        ProductImage productImage1 = ProductImage.builder()
                .id(1L)
                .imagePath("test-image.jpg")
                .build();

        ProductImage productImage2 = ProductImage.builder()
                .id(null)
                .imagePath("test-image.jpg")
                .build();

        // Act & Assert
        assertNotEquals(productImage1, productImage2);
        assertNotEquals(productImage2, productImage1);
    }

    @Test
    void equals_WithDifferentType_ReturnsFalse() {
        // Arrange
        ProductImage productImage = ProductImage.builder()
                .id(1L)
                .imagePath("test-image.jpg")
                .build();

        // Act & Assert
        assertNotEquals(productImage, "Not a product image");
    }

    @Test
    void equals_WithNull_ReturnsFalse() {
        // Arrange
        ProductImage productImage = ProductImage.builder()
                .id(1L)
                .imagePath("test-image.jpg")
                .build();

        // Act & Assert
        assertNotEquals(productImage, null);
    }

    @Test
    void toString_ContainsAllFields() {
        // Arrange
        ProductImage productImage = ProductImage.builder()
                .id(1L)
                .imagePath("test-image.jpg")
                .uploadOrder(0)
                .build();

        // Act
        String toString = productImage.toString();

        // Assert
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("imagePath=test-image.jpg"));
        assertTrue(toString.contains("uploadOrder=0"));
    }
}