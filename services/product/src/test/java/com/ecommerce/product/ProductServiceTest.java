package com.ecommerce.product;

import com.ecommerce.attribute.Attribute;
import com.ecommerce.attribute.AttributeResponse;
import com.ecommerce.attribute.AttributeService;
import com.ecommerce.exception.ProductNotFoundException;
import com.ecommerce.feignclient.category.CategoryClient;
import com.ecommerce.productimage.ProductImageResponse;
import com.ecommerce.productimage.ProductImageService;
import com.ecommerce.relatedproduct.RelatedProductService;
import com.ecommerce.settings.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ProductImageService productImageService;

    @Mock
    private RelatedProductService relatedProductService;

    @Mock
    private ProductPriceService productPriceService;

    @Mock
    private AttributeService attributeService;

    @Mock
    private CategoryClient categoryClient;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private ProductResponse testProductResponse;
    private ProductOverviewResponse testProductOverviewResponse;
    private ProductRequest testProductRequest;
    private Long testProductId;
    private Set<Long> testCategoryIds;
    private Set<Long> testAttributeIds;
    private Set<Long> testRelatedProductIds;
    private Set<Attribute> testAttributes;
    private Set<Product> testRelatedProducts;

    @BeforeEach
    void setUp() {
        testProductId = 1L;
        testCategoryIds = Set.of(1L, 2L);
        testAttributeIds = Set.of(1L, 2L);
        testRelatedProductIds = Set.of(2L, 3L);

        testProduct = Product.builder()
                .id(testProductId)
                .name("Test Product")
                .description("Test Description")
                .isVisible(true)
                .categoryIds(testCategoryIds)
                .build();

        testProductResponse = ProductResponse.builder()
                .id(testProductId)
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("100.00"))
                .basePrice(new BigDecimal("120.00"))
                .isPriceEqual(true)
                .isVisible(true)
                .categoryIds(testCategoryIds)
                .build();

        testProductOverviewResponse = ProductOverviewResponse.builder()
                .id(testProductId)
                .name("Test Product")
                .price(new BigDecimal("100.00"))
                .basePrice(new BigDecimal("120.00"))
                .isPriceEqual(true)
                .isVisible(true)
                .categoryIds(testCategoryIds)
                .build();

        testProductRequest = new ProductRequest(
                testProductId,
                "Test Product",
                "Test Description",
                true,
                testCategoryIds,
                testAttributeIds,
                testRelatedProductIds
        );

        testAttributes = new HashSet<>();
        testAttributes.add(Attribute.builder().id(1L).name("Color").build());
        testAttributes.add(Attribute.builder().id(2L).name("Size").build());

        testRelatedProducts = new HashSet<>();
        Product relatedProduct = Product.builder().id(2L).name("Related Product").build();
        testRelatedProducts.add(relatedProduct);
    }

    @Test
    void findProductEntityById_WithValidId_ReturnsProduct() {
        // Arrange
        when(productRepository.findById(testProductId)).thenReturn(Optional.of(testProduct));

        // Act
        Product result = productService.findProductEntityById(testProductId);

        // Assert
        assertNotNull(result);
        assertEquals(testProduct.getId(), result.getId());
        assertEquals(testProduct.getName(), result.getName());
        verify(productRepository).findById(testProductId);
    }

    @Test
    void findProductEntityById_WithInvalidId_ThrowsProductNotFoundException() {
        // Arrange
        Long invalidId = 999L;
        when(productRepository.findById(invalidId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> productService.findProductEntityById(invalidId));
        verify(productRepository).findById(invalidId);
    }

    @Test
    void findProductEntityById_WithNullId_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> productService.findProductEntityById(null));
        verify(productRepository, never()).findById(any());
    }

    @Test
    void getProductById_WithValidId_ReturnsProductResponse() {
        // Arrange
        when(productRepository.findById(testProductId)).thenReturn(Optional.of(testProduct));
        when(productMapper.toResponse(testProduct)).thenReturn(testProductResponse);

        // Act
        ProductResponse result = productService.getProductById(testProductId);

        // Assert
        assertNotNull(result);
        assertEquals(testProductResponse, result);
        verify(productRepository).findById(testProductId);
        verify(productMapper).toResponse(testProduct);
    }

    @Test
    void getAllProducts_WithValidPageRequest_ReturnsPageOfProductResponses() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("id"));
        Page<Product> productPage = new PageImpl<>(List.of(testProduct));

        when(productRepository.findAll(pageRequest)).thenReturn(productPage);
        when(productMapper.toOverviewResponse(testProduct)).thenReturn(testProductOverviewResponse);

        // Act
        Page<ProductOverviewResponse> result = productService.getAllProducts(pageRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testProductOverviewResponse, result.getContent().get(0));
        verify(productRepository).findAll(pageRequest);
        verify(productMapper).toOverviewResponse(testProduct);
    }

    @Test
    void getAllProducts_WithNullPageRequest_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> productService.getAllProducts(null));
        verify(productRepository, never()).findAll(any(PageRequest.class));
    }

    @Test
    void getProductsByCategory_WithValidParameters_ReturnsPageOfProductResponses() {
        // Arrange
        Long categoryId = 1L;
        ProductSpecificationRequest specifications = new ProductSpecificationRequest(
                new BigDecimal("10.00"),
                new BigDecimal("100.00"),
                Set.of(1L, 2L)
        );
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("id"));

        List<Product> products = List.of(testProduct);
        when(productRepository.findAll(any(Specification.class))).thenReturn(products);
        when(productMapper.toOverviewResponse(testProduct)).thenReturn(testProductOverviewResponse);

        // Act
        Page<ProductOverviewResponse> result = productService.getProductsByCategory(categoryId, specifications, pageRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testProductOverviewResponse, result.getContent().get(0));
        verify(productRepository).findAll(any(Specification.class));
        verify(productMapper).toOverviewResponse(testProduct);
    }

    @Test
    void getProductsByCategory_WithSortById_SortsCorrectly() {
        // Arrange
        Long categoryId = 1L;
        ProductSpecificationRequest specifications = new ProductSpecificationRequest(null, null, null);
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("id"));

        Product product1 = Product.builder().id(1L).name("A").isVisible(true).build();
        Product product2 = Product.builder().id(2L).name("B").isVisible(true).build();
        List<Product> products = List.of(product2, product1);  // Unsorted order

        when(productRepository.findAll(any(Specification.class))).thenReturn(products);
        when(productMapper.toOverviewResponse(product1)).thenReturn(ProductOverviewResponse.builder().id(1L).name("A").build());
        when(productMapper.toOverviewResponse(product2)).thenReturn(ProductOverviewResponse.builder().id(2L).name("B").build());

        // Act
        Page<ProductOverviewResponse> result = productService.getProductsByCategory(categoryId, specifications, pageRequest);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).id()); // Should be sorted by ID
        assertEquals(2L, result.getContent().get(1).id());
        verify(productRepository).findAll(any(Specification.class));
    }

    @Test
    void getProductsByCategory_WithSortByName_SortsCorrectly() {
        // Arrange
        Long categoryId = 1L;
        ProductSpecificationRequest specifications = new ProductSpecificationRequest(null, null, null);
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));

        Product product1 = Product.builder().id(1L).name("B").isVisible(true).build();
        Product product2 = Product.builder().id(2L).name("A").isVisible(true).build();
        List<Product> products = List.of(product1, product2);  // Unsorted order

        when(productRepository.findAll(any(Specification.class))).thenReturn(products);
        when(productMapper.toOverviewResponse(product1)).thenReturn(ProductOverviewResponse.builder().id(1L).name("B").build());
        when(productMapper.toOverviewResponse(product2)).thenReturn(ProductOverviewResponse.builder().id(2L).name("A").build());

        // Act
        Page<ProductOverviewResponse> result = productService.getProductsByCategory(categoryId, specifications, pageRequest);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("A", result.getContent().get(0).name()); // Should be sorted by name
        assertEquals("B", result.getContent().get(1).name());
        verify(productRepository).findAll(any(Specification.class));
    }

    @Test
    void getProductsByCategory_WithSortByPrice_SortsCorrectly() {
        // Arrange
        Long categoryId = 1L;
        ProductSpecificationRequest specifications = new ProductSpecificationRequest(null, null, null);
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("price"));

        Product product1 = Product.builder().id(1L).name("A").isVisible(true).build();
        Product product2 = Product.builder().id(2L).name("B").isVisible(true).build();
        List<Product> products = List.of(product1, product2);

        BigDecimal price1 = new BigDecimal("200.00");
        BigDecimal price2 = new BigDecimal("100.00");

        when(productRepository.findAll(any(Specification.class))).thenReturn(products);
        when(productPriceService.getCheapestVariantPrice(product1)).thenReturn(price1);
        when(productPriceService.getCheapestVariantPrice(product2)).thenReturn(price2);

        when(productMapper.toOverviewResponse(product1)).thenReturn(
                ProductOverviewResponse.builder().id(1L).name("A").price(price1).build());
        when(productMapper.toOverviewResponse(product2)).thenReturn(
                ProductOverviewResponse.builder().id(2L).name("B").price(price2).build());

        // Act
        Page<ProductOverviewResponse> result = productService.getProductsByCategory(categoryId, specifications, pageRequest);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2L, result.getContent().get(0).id()); // Product with cheaper price should be first
        assertEquals(1L, result.getContent().get(1).id());
        verify(productRepository).findAll(any(Specification.class));
        verify(productPriceService, times(2)).getCheapestVariantPrice(any(Product.class));
    }

    @Test
    void getProductsByCategory_WithInvalidSortProperty_ReturnsUnsortedList() {
        // Arrange
        Long categoryId = 1L;
        ProductSpecificationRequest specifications = new ProductSpecificationRequest(null, null, null);
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("invalid_property"));

        List<Product> products = List.of(testProduct);
        when(productRepository.findAll(any(Specification.class))).thenReturn(products);
        when(productMapper.toOverviewResponse(testProduct)).thenReturn(testProductOverviewResponse);

        // Act
        Page<ProductOverviewResponse> result = productService.getProductsByCategory(categoryId, specifications, pageRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(productRepository).findAll(any(Specification.class));
    }

    @Test
    void getProductsByIds_WithValidIds_ReturnsProductResponses() {
        // Arrange
        List<Long> ids = List.of(1L, 2L);
        Product product1 = Product.builder().id(1L).name("A").isVisible(true).build();
        Product product2 = Product.builder().id(2L).name("B").isVisible(true).build();

        when(productRepository.findAllById(ids)).thenReturn(List.of(product1, product2));
        when(productMapper.toOverviewResponse(product1)).thenReturn(
                ProductOverviewResponse.builder().id(1L).name("A").build());
        when(productMapper.toOverviewResponse(product2)).thenReturn(
                ProductOverviewResponse.builder().id(2L).name("B").build());

        // Act
        List<ProductOverviewResponse> result = productService.getProductsByIds(ids);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id()); // Should maintain original order
        assertEquals(2L, result.get(1).id());
        verify(productRepository).findAllById(ids);
        verify(productMapper, times(2)).toOverviewResponse(any(Product.class));
    }

    @Test
    void getProductsByIds_WithInvisibleProducts_FiltersThemOut() {
        // Arrange
        List<Long> ids = List.of(1L, 2L);
        Product product1 = Product.builder().id(1L).name("A").isVisible(true).build();
        Product product2 = Product.builder().id(2L).name("B").isVisible(false).build(); // Invisible

        when(productRepository.findAllById(ids)).thenReturn(List.of(product1, product2));
        when(productMapper.toOverviewResponse(product1)).thenReturn(
                ProductOverviewResponse.builder().id(1L).name("A").build());

        // Act
        List<ProductOverviewResponse> result = productService.getProductsByIds(ids);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size()); // Only the visible product
        assertEquals(1L, result.get(0).id());
        verify(productRepository).findAllById(ids);
        verify(productMapper).toOverviewResponse(product1);
        verify(productMapper, never()).toOverviewResponse(product2);
    }

    @Test
    void getProductsByIds_WithEmptyList_ReturnsEmptyList() {
        // Arrange
        List<Long> ids = Collections.emptyList();

        // Act
        List<ProductOverviewResponse> result = productService.getProductsByIds(ids);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository).findAllById(ids);
        verify(productMapper, never()).toOverviewResponse(any());
    }

    @Test
    void getProductsByIds_WithNoVisibleProducts_ReturnsEmptyList() {
        // Arrange
        List<Long> ids = List.of(1L, 2L);
        Product product1 = Product.builder().id(1L).name("A").isVisible(false).build();
        Product product2 = Product.builder().id(2L).name("B").isVisible(false).build();

        when(productRepository.findAllById(ids)).thenReturn(List.of(product1, product2));

        // Act
        List<ProductOverviewResponse> result = productService.getProductsByIds(ids);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository).findAllById(ids);
        verify(productMapper, never()).toOverviewResponse(any());
    }

    @Test
    void searchProductsByQuery_WithValidParameters_ReturnsProductResponses() {
        // Arrange
        String query = "test";
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(List.of(testProduct));

        when(productRepository.findAllVisibleBySimilarity(eq(query), anyDouble(), eq(pageRequest))).thenReturn(productPage);
        when(productMapper.toOverviewResponse(testProduct)).thenReturn(testProductOverviewResponse);

        // Act
        Page<ProductOverviewResponse> result = productService.searchProductsByQuery(query, pageRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testProductOverviewResponse, result.getContent().get(0));
        verify(productRepository).findAllVisibleBySimilarity(eq(query), anyDouble(), eq(pageRequest));
        verify(productMapper).toOverviewResponse(testProduct);
    }

    @Test
    void searchProductsByQuery_WithNullQuery_ThrowsNullPointerException() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> productService.searchProductsByQuery(null, pageRequest));
        verify(productRepository, never()).findAllVisibleBySimilarity(any(), anyDouble(), any());
    }

    @Test
    void searchProductsByQuery_WithNullPageRequest_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> productService.searchProductsByQuery("test", null));
        verify(productRepository, never()).findAllVisibleBySimilarity(any(), anyDouble(), any());
    }

    @Test
    void createProduct_WithValidRequest_ReturnsProductId() {
        // Arrange
        when(productMapper.toProduct(testProductRequest)).thenReturn(testProduct);
        when(attributeService.processProductAttributes(testProductRequest)).thenReturn(testAttributes);
        when(relatedProductService.processRelatedProducts(testProduct, testProductRequest)).thenReturn(testRelatedProducts);
        when(productRepository.save(testProduct)).thenReturn(testProduct);

        // Act
        Long result = productService.createProduct(testProductRequest);

        // Assert
        assertEquals(testProductId, result);
        verify(productMapper).toProduct(testProductRequest);
        verify(categoryClient).getCategoriesByIds(testCategoryIds);
        verify(attributeService).processProductAttributes(testProductRequest);
        verify(relatedProductService).processRelatedProducts(testProduct, testProductRequest);
        verify(productRepository).save(testProduct);

        // Verify the product was properly set up
        assertEquals(testCategoryIds, testProduct.getCategoryIds());
        assertEquals(testAttributes, testProduct.getAttributes());
        assertEquals(testRelatedProducts, testProduct.getRelatedProducts());
    }

    @Test
    void createProduct_WithNullRequest_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> productService.createProduct(null));
        verify(productMapper, never()).toProduct(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProduct_WithValidIdAndRequest_ReturnsProductId() {
        // Arrange
        Product existingProduct = Product.builder()
                .id(testProductId)
                .name("Old Name")
                .build();

        when(productRepository.findById(testProductId)).thenReturn(Optional.of(existingProduct));
        when(productMapper.toProduct(testProductRequest)).thenReturn(testProduct);
        when(attributeService.processProductAttributes(testProductRequest)).thenReturn(testAttributes);
        when(relatedProductService.processRelatedProducts(testProduct, testProductRequest)).thenReturn(testRelatedProducts);
        when(productRepository.save(testProduct)).thenReturn(testProduct);

        // Act
        Long result = productService.updateProduct(testProductId, testProductRequest);

        // Assert
        assertEquals(testProductId, result);
        verify(productRepository).findById(testProductId);
        verify(productMapper).toProduct(testProductRequest);
        verify(categoryClient).getCategoriesByIds(testCategoryIds);
        verify(attributeService).processProductAttributes(testProductRequest);
        verify(relatedProductService).processRelatedProducts(testProduct, testProductRequest);
        verify(productRepository).save(testProduct);

        // Verify the product was properly updated
        assertEquals(existingProduct.getId(), testProduct.getId());
        assertEquals(existingProduct.getVariants(), testProduct.getVariants());
        assertEquals(existingProduct.getImages(), testProduct.getImages());
        assertEquals(testCategoryIds, testProduct.getCategoryIds());
        assertEquals(testAttributes, testProduct.getAttributes());
        assertEquals(testRelatedProducts, testProduct.getRelatedProducts());
    }

    @Test
    void updateProduct_WithNullId_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> productService.updateProduct(null, testProductRequest));
        verify(productRepository, never()).findById(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProduct_WithNullRequest_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> productService.updateProduct(testProductId, null));
        verify(productRepository, never()).findById(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void deleteProductById_WithValidId_DeletesProduct() {
        // Arrange
        when(productRepository.findById(testProductId)).thenReturn(Optional.of(testProduct));
        doNothing().when(productRepository).delete(testProduct);
        doNothing().when(productImageService).deleteDirectory(testProductId);

        // Act
        productService.deleteProductById(testProductId);

        // Assert
        verify(productRepository).findById(testProductId);
        verify(productRepository).delete(testProduct);
        verify(productImageService).deleteDirectory(testProductId);
    }

    @Test
    void deleteProductById_WithNullId_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> productService.deleteProductById(null));
        verify(productRepository, never()).findById(any());
        verify(productRepository, never()).delete(any());
        verify(productImageService, never()).deleteDirectory(any());
    }

    @Test
    void uploadProductImages_WithValidIdAndFiles_ProcessesFiles() {
        // Arrange
        List<MultipartFile> files = List.of(mock(MultipartFile.class));
        when(productRepository.findById(testProductId)).thenReturn(Optional.of(testProduct));
        doNothing().when(productImageService).processFilesForProduct(testProduct, files);

        // Act
        productService.uploadProductImages(testProductId, files);

        // Assert
        verify(productRepository).findById(testProductId);
        verify(productImageService).processFilesForProduct(testProduct, files);
    }

    @Test
    void getImage_WithValidIdAndFileName_ReturnsImageResponse() {
        // Arrange
        String fileName = "image.jpg";
        Resource mockResource = mock(Resource.class);
        ProductImageResponse imageResponse = new ProductImageResponse(mockResource, MediaType.IMAGE_JPEG);

        when(productRepository.findById(testProductId)).thenReturn(Optional.of(testProduct));
        when(productImageService.getImage(testProduct, fileName)).thenReturn(imageResponse);

        // Act
        ProductImageResponse result = productService.getImage(testProductId, fileName);

        // Assert
        assertNotNull(result);
        assertEquals(imageResponse, result);
        verify(productRepository).findById(testProductId);
        verify(productImageService).getImage(testProduct, fileName);
    }

    @Test
    void getFilterRangesByCategory_WithValidCategoryId_ReturnsFilterRangesResponse() {
        // Arrange
        Long categoryId = 1L;
        List<Product> products = List.of(testProduct);
        BigDecimal lowPrice = new BigDecimal("10.00");
        BigDecimal maxPrice = new BigDecimal("100.00");
        Set<AttributeResponse> attributes = Set.of(mock(AttributeResponse.class));

        when(productRepository.findAllVisibleByCategory(categoryId)).thenReturn(products);
        when(productPriceService.getCheapestVariantPrice(products)).thenReturn(lowPrice);
        when(productPriceService.getHighestVariantPrice(products)).thenReturn(maxPrice);
        when(attributeService.getAttributesByProducts(products)).thenReturn(attributes);

        // Act
        FilterRangesResponse result = productService.getFilterRangesByCategory(categoryId);

        // Assert
        assertNotNull(result);
        assertEquals(lowPrice, result.lowPrice());
        assertEquals(maxPrice, result.maxPrice());
        assertEquals(attributes, result.attributes());
        verify(productRepository).findAllVisibleByCategory(categoryId);
        verify(productPriceService).getCheapestVariantPrice(products);
        verify(productPriceService).getHighestVariantPrice(products);
        verify(attributeService).getAttributesByProducts(products);
    }
}