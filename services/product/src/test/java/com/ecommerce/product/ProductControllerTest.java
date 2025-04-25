package com.ecommerce.product;

import com.ecommerce.exception.ProductNotFoundException;
import com.ecommerce.productimage.ProductImageResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ProductResponse testProductResponse;
    private ProductOverviewResponse testProductOverviewResponse;
    private ProductRequest testProductRequest;

    @BeforeEach
    void setUp() {
        testProductResponse = ProductResponse.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("100.00"))
                .basePrice(new BigDecimal("120.00"))
                .isPriceEqual(true)
                .isVisible(true)
                .categoryIds(Set.of(1L, 2L))
                .build();

        testProductOverviewResponse = ProductOverviewResponse.builder()
                .id(1L)
                .name("Test Product")
                .price(new BigDecimal("100.00"))
                .basePrice(new BigDecimal("120.00"))
                .isPriceEqual(true)
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
    }

    @Test
    void getProductById_WithValidId_ReturnsOkWithProduct() {
        // Arrange
        when(productService.getProductById(1L)).thenReturn(testProductResponse);

        // Act
        ResponseEntity<ProductResponse> response = productController.getProductById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testProductResponse, response.getBody());
        verify(productService).getProductById(1L);
    }

    @Test
    void getProductById_WithInvalidId_ThrowsProductNotFoundException() {
        // Arrange
        when(productService.getProductById(999L)).thenThrow(new ProductNotFoundException("Product not found"));

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> productController.getProductById(999L));
        verify(productService).getProductById(999L);
    }

    @Test
    void getAllProducts_WithDefaultPagination_ReturnsOkWithProducts() {
        // Arrange
        Page<ProductOverviewResponse> productPage = new PageImpl<>(List.of(testProductOverviewResponse));
        when(productService.getAllProducts(any(PageRequest.class))).thenReturn(productPage);

        // Act
        ResponseEntity<Page<ProductOverviewResponse>> response = productController.getAllProducts(0, 10, "ASC", "id");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productPage, response.getBody());
        verify(productService).getAllProducts(any(PageRequest.class));
    }

    @Test
    void getProductsByCategory_WithDefaultPagination_ReturnsOkWithProducts() {
        // Arrange
        Page<ProductOverviewResponse> productPage = new PageImpl<>(List.of(testProductOverviewResponse));
        when(productService.getProductsByCategory(eq(1L), any(ProductSpecificationRequest.class), any(PageRequest.class)))
                .thenReturn(productPage);

        // Act
        ResponseEntity<Page<ProductOverviewResponse>> response = productController.getProductsByCategory(
                0, 10, "ASC", "id", null, null, null, 1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productPage, response.getBody());
        verify(productService).getProductsByCategory(eq(1L), any(ProductSpecificationRequest.class), any(PageRequest.class));
    }

    @Test
    void getFilterRangesByCategory_ReturnsOkWithRanges() {
        // Arrange
        FilterRangesResponse rangesResponse = FilterRangesResponse.builder()
                .lowPrice(new BigDecimal("10.00"))
                .maxPrice(new BigDecimal("100.00"))
                .build();
        when(productService.getFilterRangesByCategory(1L)).thenReturn(rangesResponse);

        // Act
        ResponseEntity<FilterRangesResponse> response = productController.getFilterRangesByCategory(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rangesResponse, response.getBody());
        verify(productService).getFilterRangesByCategory(1L);
    }

    @Test
    void getProductsByIds_WithValidIds_ReturnsOkWithProducts() {
        // Arrange
        List<Long> ids = List.of(1L, 2L);
        List<ProductOverviewResponse> products = List.of(testProductOverviewResponse);
        when(productService.getProductsByIds(ids)).thenReturn(products);

        // Act
        ResponseEntity<List<ProductOverviewResponse>> response = productController.getProductsByIds(ids);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(products, response.getBody());
        verify(productService).getProductsByIds(ids);
    }

    @Test
    void searchProductsByQuery_ReturnsOkWithProducts() {
        // Arrange
        String query = "test";
        Page<ProductOverviewResponse> productPage = new PageImpl<>(List.of(testProductOverviewResponse));
        when(productService.searchProductsByQuery(eq(query), any(PageRequest.class))).thenReturn(productPage);

        // Act
        ResponseEntity<Page<ProductOverviewResponse>> response = productController.searchProductsByQuery(0, 10, query);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productPage, response.getBody());
        verify(productService).searchProductsByQuery(eq(query), any(PageRequest.class));
    }

    @Test
    void createProduct_WithValidRequest_ReturnsCreatedWithId() {
        // Arrange
        Long createdId = 1L;
        when(productService.createProduct(testProductRequest)).thenReturn(createdId);

        // Act
        ResponseEntity<Long> response = productController.createProduct(testProductRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdId, response.getBody());
        verify(productService).createProduct(testProductRequest);
    }

    @Test
    void updateProduct_WithValidIdAndRequest_ReturnsOkWithId() {
        // Arrange
        Long updatedId = 1L;
        when(productService.updateProduct(1L, testProductRequest)).thenReturn(updatedId);

        // Act
        ResponseEntity<Long> response = productController.updateProduct(1L, testProductRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedId, response.getBody());
        verify(productService).updateProduct(1L, testProductRequest);
    }

    @Test
    void deleteProductById_WithValidId_ReturnsNoContent() {
        // Arrange
        doNothing().when(productService).deleteProductById(1L);

        // Act
        ResponseEntity<Void> response = productController.deleteProductById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(productService).deleteProductById(1L);
    }

    @Test
    void uploadProductImages_WithValidIdAndFiles_ReturnsAccepted() {
        // Arrange
        Long productId = 1L;
        List<MultipartFile> files = List.of(
                new MockMultipartFile("file1", "file1.jpg", MediaType.IMAGE_JPEG_VALUE, "test image 1".getBytes()),
                new MockMultipartFile("file2", "file2.jpg", MediaType.IMAGE_JPEG_VALUE, "test image 2".getBytes())
        );
        doNothing().when(productService).uploadProductImages(productId, files);

        // Act
        ResponseEntity<Void> response = productController.uploadProductImages(productId, files);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNull(response.getBody());
        verify(productService).uploadProductImages(productId, files);
    }

    @Test
    void getImage_WithExistingImage_ReturnsOkWithResource() {
        // Arrange
        Long productId = 1L;
        String fileName = "image.jpg";
        Resource mockResource = mock(Resource.class);
        ProductImageResponse imageResponse = new ProductImageResponse(mockResource, MediaType.IMAGE_JPEG);

        when(productService.getImage(productId, fileName)).thenReturn(imageResponse);

        // Act
        ResponseEntity<Resource> response = productController.getImage(productId, fileName);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResource, response.getBody());
        assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
        verify(productService).getImage(productId, fileName);
    }

    @Test
    void getImage_WithNonExistingImage_ReturnsNotFound() {
        // Arrange
        Long productId = 1L;
        String fileName = "nonexistent.jpg";

        when(productService.getImage(productId, fileName)).thenReturn(null);

        // Act
        ResponseEntity<Resource> response = productController.getImage(productId, fileName);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(productService).getImage(productId, fileName);
    }
}