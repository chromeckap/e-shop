package com.ecommerce.productimage;

import com.ecommerce.product.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.io.File.separator;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductImageService {
    private final ProductImageRepository productImageRepository;

    @Value("${upload.directory}")
    private String uploadDirectory;

    /**
     * Retrieves an image for a specific product.
     *
     * @param product the product entity
     * @param fileName the name of the image file
     * @return ProductImageResponse containing the resource and media type
     */
    public ProductImageResponse getImage(Product product, String fileName) {
        try {
            Path filePath = Paths.get(uploadDirectory + "/products/" + product.getId())
                    .resolve(fileName)
                    .normalize();
            UrlResource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return null;
            }

            String contentType = Files.probeContentType(filePath);
            MediaType mediaType = MediaType.parseMediaType(contentType);

            return ProductImageResponse.builder()
                    .resource(resource)
                    .mediaType(mediaType)
                    .build();

        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Processes image files for a product, handling new uploads, updates, and deletions.
     *
     * @param product the product entity
     * @param files list of image files to process
     */
    public void processFilesForProduct(Product product, List<MultipartFile> files) {
        List<ProductImage> existingProductImages = productImageRepository.findAllByProduct(product);
        Map<String, ProductImage> existingImagesMap = existingProductImages.stream()
                .collect(Collectors.toMap(
                        ProductImage::getImagePath,
                        image -> image
                ));

        List<ProductImage> imagesToSave = new ArrayList<>();

        int counter = 0;
        for (MultipartFile file : files) {
            String filename = file.getOriginalFilename();

            if (existingImagesMap.containsKey(filename)) {
                ProductImage existingImage = existingImagesMap.get(filename);
                existingImage.setUploadOrder(counter);

                imagesToSave.add(existingImage);
                existingImagesMap.remove(filename);
            } else {
                ProductImage newImage = this.saveFile(file, counter, product);
                imagesToSave.add(newImage);
            }
            counter++;
        }

        productImageRepository.saveAll(imagesToSave);

        if (!existingImagesMap.isEmpty()) {
            existingImagesMap.values().forEach(image -> {
                String path = uploadDirectory + separator + "products" + separator + product.getId() + separator + image.getImagePath();
                this.deleteFile(path);
            });

            productImageRepository.deleteAll(existingImagesMap.values());
        }
    }

    /**
     * Saves a new image file for a product.
     *
     * @param sourceFile the source file to save
     * @param counter the upload order
     * @param product the product entity
     * @return the created ProductImage entity
     */
    private ProductImage saveFile(MultipartFile sourceFile, int counter, Product product) {
        String fileUploadSubPath = "products" + separator + product.getId();
        String imagePath = this.uploadFile(sourceFile, fileUploadSubPath);
        return ProductImage.builder()
                .product(product)
                .imagePath(imagePath)
                .uploadOrder(counter)
                .build();
    }

    /**
     * Uploads a file to the specified sub-path.
     *
     * @param sourceFile the source file to upload
     * @param fileUploadSubPath the sub-path to upload to
     * @return the filename of the uploaded file
     */
    private String uploadFile(MultipartFile sourceFile, String fileUploadSubPath) {
        String finalUploadPath = uploadDirectory + separator + fileUploadSubPath;
        File file = new File(finalUploadPath);

        if (!file.exists()) {
            boolean folderCreated = file.mkdirs();
            if (!folderCreated) {
                log.warn("Failed to create the target folder: " + file);
                return null;
            }
        }
        String targetFilePath = finalUploadPath + separator + sourceFile.getOriginalFilename();
        Path targetPath = Paths.get(targetFilePath);
        try {
            Files.write(targetPath, sourceFile.getBytes());
            log.info("File saved to: " + targetFilePath);
            return sourceFile.getOriginalFilename();
        } catch (IOException e) {
            log.error("Failed to saved a file: ", e);
        }
        return null;
    }

    /**
     * Deletes product image from the directory.
     *
     * @param imagePath path to the picture
     */
    private void deleteFile(String imagePath) {
        File file = new File(imagePath);

        if (file.exists() && file.isFile()) {
            boolean fileDeleted = file.delete();
            if (!fileDeleted) {
                log.warn("Failed to delete file: " + imagePath);
            }
        }
    }

    /**
     * Deletes all product images and the product directory.
     *
     * @param productId the product ID
     */
    public void deleteDirectory(Long productId) {
        String directoryPath = uploadDirectory + separator + "products" + separator + productId;
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            deleteFilesRecursively(directory);

            boolean dirDeleted = directory.delete();
            if (!dirDeleted) {
                log.warn("Failed to delete directory: " + directory.getAbsolutePath());
            }
        } else {
            log.warn("Directory does not exist or is not a directory: " + directory.getAbsolutePath());
        }
    }

    /**
     * Deletes product image from the directory.
     *
     * @param file the required file
     */
    private void deleteFilesRecursively(File file) {
        if (file.isDirectory()) {
            for (File subFile : Objects.requireNonNull(file.listFiles())) {
                deleteFilesRecursively(subFile);
            }
        }
        try {
            Files.delete(file.toPath());
        } catch (IOException e) {
            log.error("Error deleting file: " + file.getAbsolutePath(), e);
        }
    }
}
