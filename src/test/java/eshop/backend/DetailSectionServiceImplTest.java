package eshop.backend;

import eshop.backend.exception.DetailSectionNotFoundException;
import eshop.backend.exception.ProductNotFoundException;
import eshop.backend.model.DetailSection;
import eshop.backend.model.Product;
import eshop.backend.repository.DetailSectionRepository;
import eshop.backend.repository.ProductRepository;
import eshop.backend.request.DetailSectionRequest;
import eshop.backend.service.DetailSectionService;
import eshop.backend.service.impl.DetailSectionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DetailSectionServiceImplTest {

    @Mock
    private DetailSectionRepository detailSectionRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private DetailSectionServiceImpl detailSectionService;

    private Product product;
    private DetailSection detailSection;
    private DetailSectionRequest request;

    @BeforeEach
    void setup() {
        product = new Product();
        product.setId(0L); // Ensure this matches the productId you will use in the request
        detailSection = new DetailSection(1L, ",", "x", product);
        request = new DetailSectionRequest(1L, ",", "x", 0L); // Set a valid productId
    }

    @Test
    void createDetailSection() throws ProductNotFoundException {
        // Given
        when(productRepository.findById(request.productId())).thenReturn(java.util.Optional.of(product));
        when(detailSectionRepository.save(any(DetailSection.class))).thenReturn(detailSection); // Mock save to return the created detailSection

        // When
        DetailSection createdDetailSection = detailSectionService.createDetailSection(request);

        // Then
        assertNotNull(createdDetailSection);
        verify(detailSectionRepository).save(any(DetailSection.class)); // Verify that save was called
        assertEquals(request.title(), createdDetailSection.getTitle());
        assertEquals(request.description(), createdDetailSection.getDescription());
        assertEquals(product, createdDetailSection.getProduct());
    }

    @Test
    void createDetailSection_ProductNotFoundException() {
        // Given
        when(productRepository.findById(request.productId())).thenReturn(java.util.Optional.empty());

        // When & Then
        assertThrows(ProductNotFoundException.class, () -> detailSectionService.createDetailSection(request));
    }

    @Test
    void updateDetailSection() throws DetailSectionNotFoundException {
        // Given
        when(detailSectionRepository.findById(request.id())).thenReturn(java.util.Optional.of(detailSection));

        // When
        DetailSection updatedDetailSection = detailSectionService.updateDetailSection(request);

        // Then
        assertNotNull(updatedDetailSection);
        assertEquals(request.title(), updatedDetailSection.getTitle());
        assertEquals(request.description(), updatedDetailSection.getDescription());
        verify(detailSectionRepository).save(updatedDetailSection);
    }

    @Test
    void updateDetailSection_DetailSectionNotFoundException() {
        // Given
        when(detailSectionRepository.findById(request.id())).thenReturn(java.util.Optional.empty());

        // When & Then
        assertThrows(DetailSectionNotFoundException.class, () -> detailSectionService.updateDetailSection(request));
    }

    @Test
    void getDetailSection() throws DetailSectionNotFoundException {
        // Given
        when(detailSectionRepository.findById(request.id())).thenReturn(java.util.Optional.of(detailSection));

        // When
        DetailSection retrievedDetailSection = detailSectionService.getDetailSection(request.id());

        // Then
        assertNotNull(retrievedDetailSection);
        assertEquals(detailSection, retrievedDetailSection);
    }

    @Test
    void getDetailSection_DetailSectionNotFoundException() {
        // Given
        when(detailSectionRepository.findById(request.id())).thenReturn(java.util.Optional.empty());

        // When & Then
        assertThrows(DetailSectionNotFoundException.class, () -> detailSectionService.getDetailSection(request.id()));
    }

    @Test
    void deleteDetailSection() throws DetailSectionNotFoundException {
        // Given
        when(detailSectionRepository.findById(request.id())).thenReturn(java.util.Optional.of(detailSection));

        // When
        detailSectionService.deleteDetailSection(request.id());

        // Then
        verify(detailSectionRepository).delete(detailSection);
    }

    @Test
    void deleteDetailSection_DetailSectionNotFoundException() {
        // Given
        when(detailSectionRepository.findById(request.id())).thenReturn(java.util.Optional.empty());

        // When & Then
        assertThrows(DetailSectionNotFoundException.class, () -> detailSectionService.deleteDetailSection(request.id()));
    }
}
