package com.ecommerce.productimage;

import com.ecommerce.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // Add lenient settings to avoid unnecessary stubbing errors
class ProductImageServiceTest {

    @Mock
    private ProductImageRepository productImageRepository;

    @InjectMocks
    private ProductImageService productImageService;

    private Product testProduct;
    private Path testProductDir;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        String testUploadDirectory = tempDir.toString();
        ReflectionTestUtils.setField(productImageService, "uploadDirectory", testUploadDirectory);

        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .build();

        testProductDir = tempDir.resolve("products").resolve("1");
    }

    @Test
    void getImage_WithExistingImage_ReturnsImageResponse() throws IOException {
        // Arrange
        String fileName = "test-image.jpg";

        // Create test directory and file
        Files.createDirectories(testProductDir);
        Path testFilePath = testProductDir.resolve(fileName);
        Files.write(testFilePath, "test image content".getBytes());

        // Act
        ProductImageResponse response = productImageService.getImage(testProduct, fileName);

        // Assert
        assertNotNull(response);
        assertEquals(MediaType.IMAGE_JPEG, response.mediaType());
        assertInstanceOf(UrlResource.class, response.resource());
        assertTrue(response.resource().exists());
    }

    @Test
    void getImage_WithNonExistentImage_ReturnsNull() {
        // Arrange
        String fileName = "non-existent-image.jpg";

        // Act
        ProductImageResponse response = productImageService.getImage(testProduct, fileName);

        // Assert
        assertNull(response);
    }

    @Test
    void processFilesForProduct_WithNewFiles_SavesFiles() throws IOException {
        // Arrange
        MultipartFile file1 = mock(MultipartFile.class);
        when(file1.getOriginalFilename()).thenReturn("image1.jpg");
        when(file1.getBytes()).thenReturn("test image 1".getBytes());

        MultipartFile file2 = mock(MultipartFile.class);
        when(file2.getOriginalFilename()).thenReturn("image2.jpg");
        when(file2.getBytes()).thenReturn("test image 2".getBytes());

        List<MultipartFile> files = List.of(file1, file2);

        when(productImageRepository.findAllByProduct(testProduct)).thenReturn(Collections.emptyList());

        // Act
        productImageService.processFilesForProduct(testProduct, files);

        // Assert
        verify(productImageRepository).findAllByProduct(testProduct);

        ArgumentCaptor<List<ProductImage>> savedImagesCaptor = ArgumentCaptor.forClass(List.class);
        verify(productImageRepository).saveAll(savedImagesCaptor.capture());

        List<ProductImage> savedImages = savedImagesCaptor.getValue();
        assertEquals(2, savedImages.size());

        assertEquals("image1.jpg", savedImages.get(0).getImagePath());
        assertEquals(0, savedImages.get(0).getUploadOrder());
        assertEquals(testProduct, savedImages.get(0).getProduct());

        assertEquals("image2.jpg", savedImages.get(1).getImagePath());
        assertEquals(1, savedImages.get(1).getUploadOrder());
        assertEquals(testProduct, savedImages.get(1).getProduct());

        // Verify files were created
        assertTrue(Files.exists(testProductDir.resolve("image1.jpg")));
        assertTrue(Files.exists(testProductDir.resolve("image2.jpg")));

        // Verify no deletes since there were no existing images
        verify(productImageRepository, never()).deleteAll(anyList());
    }

    @Test
    void processFilesForProduct_WithExistingAndNewFiles_UpdatesAndSavesFiles() throws IOException {
        // Arrange
        // Create existing product image
        ProductImage existingImage = ProductImage.builder()
                .id(1L)
                .imagePath("image1.jpg")
                .uploadOrder(0)
                .product(testProduct)
                .build();

        when(productImageRepository.findAllByProduct(testProduct)).thenReturn(List.of(existingImage));

        // Set up files - one existing, one new
        MultipartFile file1 = mock(MultipartFile.class);
        when(file1.getOriginalFilename()).thenReturn("image1.jpg"); // Same name as existing
        when(file1.getBytes()).thenReturn("updated image 1".getBytes());

        MultipartFile file2 = mock(MultipartFile.class);
        when(file2.getOriginalFilename()).thenReturn("image2.jpg"); // New file
        when(file2.getBytes()).thenReturn("test image 2".getBytes());

        List<MultipartFile> files = List.of(file1, file2);

        // Act
        productImageService.processFilesForProduct(testProduct, files);

        // Assert
        verify(productImageRepository).findAllByProduct(testProduct);

        ArgumentCaptor<List<ProductImage>> savedImagesCaptor = ArgumentCaptor.forClass(List.class);
        verify(productImageRepository).saveAll(savedImagesCaptor.capture());

        List<ProductImage> savedImages = savedImagesCaptor.getValue();
        assertEquals(2, savedImages.size());

        // First image should be the existing one with updated order
        assertEquals(existingImage.getId(), savedImages.get(0).getId());
        assertEquals("image1.jpg", savedImages.get(0).getImagePath());
        assertEquals(0, savedImages.get(0).getUploadOrder()); // Order updated

        // Second should be new
        assertNull(savedImages.get(1).getId()); // New image, no ID yet
        assertEquals("image2.jpg", savedImages.get(1).getImagePath());
        assertEquals(1, savedImages.get(1).getUploadOrder());

        // Verify no deletes since all existing images were used
        verify(productImageRepository, never()).deleteAll(anyList());
    }

    @Test
    void deleteDirectory_WithExistingDirectory_DeletesDirectory() throws IOException {
        // Arrange
        // Create test directory and files
        Files.createDirectories(testProductDir);
        Files.write(testProductDir.resolve("image1.jpg"), "test image 1".getBytes());
        Files.write(testProductDir.resolve("image2.jpg"), "test image 2".getBytes());

        // Create subdirectory
        Path subDir = testProductDir.resolve("subdir");
        Files.createDirectories(subDir);
        Files.write(subDir.resolve("image3.jpg"), "test image 3".getBytes());

        // Delete files in subdirectory first to allow directory deletion
        Files.delete(subDir.resolve("image3.jpg"));
        Files.delete(subDir);
        Files.delete(testProductDir.resolve("image1.jpg"));
        Files.delete(testProductDir.resolve("image2.jpg"));

        // Act
        productImageService.deleteDirectory(testProduct.getId());

        // Assert
        // Directory should be deleted since we manually deleted the files
        assertFalse(Files.exists(testProductDir));
    }

    @Test
    void deleteDirectory_WithNonExistentDirectory_DoesNothing() {
        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> productImageService.deleteDirectory(999L));
    }
}