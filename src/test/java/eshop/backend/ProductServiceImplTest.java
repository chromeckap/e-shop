package eshop.backend;

import eshop.backend.exception.CategoryNotFoundException;
import eshop.backend.exception.ProductNotFoundException;
import eshop.backend.model.Category;
import eshop.backend.model.Product;
import eshop.backend.repository.CategoryRepository;
import eshop.backend.repository.ProductRepository;
import eshop.backend.request.ProductRequest;
import eshop.backend.response.ProductResponse;
import eshop.backend.service.ReviewService;
import eshop.backend.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ReviewService reviewService; // Mock the ReviewService

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductRequest productRequest;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productRequest = new ProductRequest(1L, null, null, null, null, null); // Initialize with necessary fields
        product = new Product(productRequest);
        product.setId(1L); // Set an ID for the product
    }

    @Test
    void testCreateProduct() throws CategoryNotFoundException {
        when(categoryRepository.findById(any())).thenReturn(Optional.of(new Category()));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product createdProduct = productService.createProduct(productRequest);

        assertNotNull(createdProduct);
        assertEquals(product.getId(), createdProduct.getId());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProduct() throws ProductNotFoundException, CategoryNotFoundException {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductRequest updateRequest = new ProductRequest(1L, null, null, null, null, null); // Initialize with updated fields

        Product updatedProduct = productService.updateProduct(updateRequest);

        assertNotNull(updatedProduct);
        assertEquals(product.getId(), updatedProduct.getId());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testGetProduct() throws ProductNotFoundException {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse response = productService.getProduct(1L);

        assertNotNull(response);
        assertEquals(product.getId(), response.getProductId());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteProduct() throws ProductNotFoundException {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void testPageOfAllProducts() {
        // Assuming you have a method to create a Page<Product>
        // This is an example placeholder; you'll need to implement the actual pagination logic
        Page<Product> productPage = new PageImpl<>(List.of(product));
        when(productRepository.findAll(any(PageRequest.class))).thenReturn(productPage);

        Page<Product> result = productService.pageOfAllProducts(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(productRepository, times(1)).findAll(any(PageRequest.class));
    }
}
