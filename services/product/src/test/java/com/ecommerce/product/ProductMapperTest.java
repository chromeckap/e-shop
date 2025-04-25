package com.ecommerce.product;

import com.ecommerce.attribute.Attribute;
import com.ecommerce.attribute.AttributeMapper;
import com.ecommerce.attribute.AttributeResponse;
import com.ecommerce.productimage.ProductImage;
import com.ecommerce.variant.Variant;
import com.ecommerce.variant.VariantMapper;
import com.ecommerce.variant.VariantResponse;
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
class ProductMapperTest {

    @Mock
    private VariantMapper variantMapper;

    @Mock
    private AttributeMapper attributeMapper;

    @Mock
    private ProductPriceService productPriceService;

    @InjectMocks
    private ProductMapper productMapper;

    private Product testProduct;
    private ProductRequest testProductRequest;
    private Variant testVariant;
    private VariantResponse testVariantResponse;
    private Attribute testAttribute;
    private AttributeResponse testAttributeResponse;
    private ProductImage testProductImage;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .isVisible(true)
                .categoryIds(Set.of(1L, 2L))
                .build();

        testProductRequest = new ProductRequest(
                1L,
                "Test Product",
                "Test Description",
                true,
                Set.of(1L, 2L),
                Set.of(1L, 2L),
                Set.of(2L, 3L)
        );

        testVariant = new Variant();
        testVariant.setId(1L);
        testVariant.setDiscountedPrice(new BigDecimal("100.00"));
        testVariant.setBasePrice(new BigDecimal("120.00"));
        testVariant.setProduct(testProduct);

        testVariantResponse = VariantResponse.builder()
                .id(1L)
                .discountedPrice(new BigDecimal("100.00"))
                .basePrice(new BigDecimal("120.00"))
                .build();

        testAttribute = Attribute.builder()
                .id(1L)
                .name("Color")
                .build();

        testAttributeResponse = AttributeResponse.builder()
                .id(1L)
                .name("Color")
                .values(Collections.emptyList())
                .build();

        testProductImage = new ProductImage();
        testProductImage.setId(1L);
        testProductImage.setImagePath("/images/products/1/image1.jpg");
        testProductImage.setProduct(testProduct);
    }

    @Test
    void toProduct_WithValidRequest_MapsCorrectly() {
        // Act
        Product result = productMapper.toProduct(testProductRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testProductRequest.id(), result.getId());
        assertEquals(testProductRequest.name(), result.getName());
        assertEquals(testProductRequest.description(), result.getDescription());
        assertEquals(testProductRequest.isVisible(), result.isVisible());
        // Note: attributes, related products and category ids are not set by the mapper directly
    }

    @Test
    void toProduct_WithNullRequest_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> productMapper.toProduct(null));
    }

    @Test
    void toOverviewResponse_WithValidProduct_MapsCorrectly() {
        // Arrange
        testProduct.setVariants(List.of(testVariant));

        List<ProductImage> images = new ArrayList<>();
        images.add(testProductImage);
        testProduct.setImages(images);

        Product relatedProduct = Product.builder().id(2L).name("Related Product").build();
        testProduct.setRelatedProducts(Set.of(relatedProduct));

        when(productPriceService.getCheapestVariantPrice(testProduct)).thenReturn(new BigDecimal("100.00"));
        when(productPriceService.getCheapestVariantBasePrice(testProduct)).thenReturn(new BigDecimal("120.00"));
        when(productPriceService.isVariantsPricesEqual(testProduct)).thenReturn(true);

        // Act
        ProductOverviewResponse result = productMapper.toOverviewResponse(testProduct);

        // Assert
        assertNotNull(result);
        assertEquals(testProduct.getId(), result.id());
        assertEquals(testProduct.getName(), result.name());
        assertEquals(new BigDecimal("100.00"), result.price());
        assertEquals(new BigDecimal("120.00"), result.basePrice());
        assertTrue(result.isPriceEqual());
        assertEquals(testProduct.isVisible(), result.isVisible());
        assertEquals(testProduct.getCategoryIds(), result.categoryIds());
        assertEquals(1, result.relatedProductIds().size());
        assertTrue(result.relatedProductIds().contains(2L));
        assertEquals("/images/products/1/image1.jpg", result.primaryImagePath());

        verify(productPriceService).getCheapestVariantPrice(testProduct);
        verify(productPriceService).getCheapestVariantBasePrice(testProduct);
        verify(productPriceService).isVariantsPricesEqual(testProduct);
    }

    @Test
    void toOverviewResponse_WithNoVariants_MapsCorrectly() {
        // Arrange
        testProduct.setVariants(Collections.emptyList());

        when(productPriceService.getCheapestVariantPrice(testProduct)).thenReturn(null);
        when(productPriceService.getCheapestVariantBasePrice(testProduct)).thenReturn(null);
        when(productPriceService.isVariantsPricesEqual(testProduct)).thenReturn(true);

        // Act
        ProductOverviewResponse result = productMapper.toOverviewResponse(testProduct);

        // Assert
        assertNotNull(result);
        assertEquals(testProduct.getId(), result.id());
        assertEquals(testProduct.getName(), result.name());
        assertNull(result.price());
        assertNull(result.basePrice());
        assertTrue(result.isPriceEqual());

        verify(productPriceService).getCheapestVariantPrice(testProduct);
        verify(productPriceService).getCheapestVariantBasePrice(testProduct);
        verify(productPriceService).isVariantsPricesEqual(testProduct);
    }

    @Test
    void toOverviewResponse_WithNoImages_MapsCorrectly() {
        // Arrange
        testProduct.setImages(Collections.emptyList());

        when(productPriceService.getCheapestVariantPrice(testProduct)).thenReturn(new BigDecimal("100.00"));
        when(productPriceService.getCheapestVariantBasePrice(testProduct)).thenReturn(new BigDecimal("120.00"));
        when(productPriceService.isVariantsPricesEqual(testProduct)).thenReturn(true);

        // Act
        ProductOverviewResponse result = productMapper.toOverviewResponse(testProduct);

        // Assert
        assertNotNull(result);
        assertNull(result.primaryImagePath());
    }

    @Test
    void toOverviewResponse_WithNullRequest_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> productMapper.toOverviewResponse(null));
    }

    @Test
    void toResponse_WithValidProduct_MapsCorrectly() {
        // Arrange
        testProduct.setVariants(List.of(testVariant));
        testProduct.setAttributes(Set.of(testAttribute));

        List<ProductImage> images = new ArrayList<>();
        images.add(testProductImage);
        testProduct.setImages(images);

        Product relatedProduct = Product.builder().id(2L).name("Related Product").build();
        testProduct.setRelatedProducts(Set.of(relatedProduct));

        when(productPriceService.getCheapestVariantPrice(testProduct)).thenReturn(new BigDecimal("100.00"));
        when(productPriceService.getCheapestVariantBasePrice(testProduct)).thenReturn(new BigDecimal("120.00"));
        when(productPriceService.isVariantsPricesEqual(testProduct)).thenReturn(true);
        when(variantMapper.toResponse(testVariant)).thenReturn(testVariantResponse);
        when(attributeMapper.toResponse(testAttribute)).thenReturn(testAttributeResponse);
        when(productPriceService.getCheapestVariantPrice(relatedProduct)).thenReturn(new BigDecimal("150.00"));
        when(productPriceService.getCheapestVariantBasePrice(relatedProduct)).thenReturn(new BigDecimal("170.00"));
        when(productPriceService.isVariantsPricesEqual(relatedProduct)).thenReturn(true);

        // Act
        ProductResponse result = productMapper.toResponse(testProduct);

        // Assert
        assertNotNull(result);
        assertEquals(testProduct.getId(), result.id());
        assertEquals(testProduct.getName(), result.name());
        assertEquals(testProduct.getDescription(), result.description());
        assertEquals(new BigDecimal("100.00"), result.price());
        assertEquals(new BigDecimal("120.00"), result.basePrice());
        assertTrue(result.isPriceEqual());
        assertEquals(testProduct.isVisible(), result.isVisible());
        assertEquals(testProduct.getCategoryIds(), result.categoryIds());

        assertNotNull(result.variants());
        assertEquals(1, result.variants().size());
        assertTrue(result.variants().contains(testVariantResponse));

        assertNotNull(result.relatedProducts());
        assertEquals(1, result.relatedProducts().size());

        assertNotNull(result.attributes());
        assertEquals(1, result.attributes().size());
        assertTrue(result.attributes().contains(testAttributeResponse));

        assertNotNull(result.imagePaths());
        assertEquals(1, result.imagePaths().size());
        assertEquals("/images/products/1/image1.jpg", result.imagePaths().getFirst());

        verify(productPriceService).getCheapestVariantPrice(testProduct);
        verify(productPriceService).getCheapestVariantBasePrice(testProduct);
        verify(productPriceService).isVariantsPricesEqual(testProduct);
        verify(variantMapper).toResponse(testVariant);
        verify(attributeMapper).toResponse(testAttribute);
    }

    @Test
    void toResponse_WithNoVariants_MapsCorrectly() {
        // Arrange
        testProduct.setVariants(Collections.emptyList());

        when(productPriceService.getCheapestVariantPrice(testProduct)).thenReturn(null);
        when(productPriceService.getCheapestVariantBasePrice(testProduct)).thenReturn(null);
        when(productPriceService.isVariantsPricesEqual(testProduct)).thenReturn(true);

        // Act
        ProductResponse result = productMapper.toResponse(testProduct);

        // Assert
        assertNotNull(result);
        assertNull(result.price());
        assertNull(result.basePrice());
        assertTrue(result.isPriceEqual());
        assertNotNull(result.variants());
        assertTrue(result.variants().isEmpty());
    }

    @Test
    void toResponse_WithNoRelatedProducts_MapsCorrectly() {
        // Arrange
        testProduct.setRelatedProducts(Collections.emptySet());

        when(productPriceService.getCheapestVariantPrice(testProduct)).thenReturn(new BigDecimal("100.00"));
        when(productPriceService.getCheapestVariantBasePrice(testProduct)).thenReturn(new BigDecimal("120.00"));
        when(productPriceService.isVariantsPricesEqual(testProduct)).thenReturn(true);

        // Act
        ProductResponse result = productMapper.toResponse(testProduct);

        // Assert
        assertNotNull(result);
        assertNotNull(result.relatedProducts());
        assertTrue(result.relatedProducts().isEmpty());
    }

    @Test
    void toResponse_WithNoAttributes_MapsCorrectly() {
        // Arrange
        testProduct.setAttributes(Collections.emptySet());

        when(productPriceService.getCheapestVariantPrice(testProduct)).thenReturn(new BigDecimal("100.00"));
        when(productPriceService.getCheapestVariantBasePrice(testProduct)).thenReturn(new BigDecimal("120.00"));
        when(productPriceService.isVariantsPricesEqual(testProduct)).thenReturn(true);

        // Act
        ProductResponse result = productMapper.toResponse(testProduct);

        // Assert
        assertNotNull(result);
        assertNotNull(result.attributes());
        assertTrue(result.attributes().isEmpty());
    }

    @Test
    void toResponse_WithNoImages_ReturnsNullImagePaths() {
        // Arrange
        testProduct.setImages(Collections.emptyList());

        when(productPriceService.getCheapestVariantPrice(testProduct)).thenReturn(new BigDecimal("100.00"));
        when(productPriceService.getCheapestVariantBasePrice(testProduct)).thenReturn(new BigDecimal("120.00"));
        when(productPriceService.isVariantsPricesEqual(testProduct)).thenReturn(true);

        // Act
        ProductResponse result = productMapper.toResponse(testProduct);

        // Assert
        assertNotNull(result);
        assertNull(result.imagePaths());
    }

    @Test
    void toResponse_WithNullRequest_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> productMapper.toResponse(null));
    }
}