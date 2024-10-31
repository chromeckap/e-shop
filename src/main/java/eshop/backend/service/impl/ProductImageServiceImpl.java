package eshop.backend.service.impl;

import eshop.backend.model.Product;
import eshop.backend.model.ProductImage;
import eshop.backend.repository.ProductImageRepository;
import eshop.backend.repository.ProductRepository;
import eshop.backend.request.ProductImageRequest;
import eshop.backend.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;

@Service
@RequiredArgsConstructor // otherwise has problem with @value
public class ProductImageServiceImpl implements ProductImageService {
    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;

    @Value("${file.upload-dir}")
    private String uploadDirectory;

    public void manageProductPictures(Long productId, @RequestParam("files") Set<ProductImageRequest> files) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow();
        Set<Long> originalImages = productImageRepository.findAllIdsByProduct(product);

        int counter = 0;
        for (ProductImageRequest file : files) {
            if (file.id() != null && !originalImages.contains(file.id())) {
                deleteImage(file.id());
                continue;
            }

            ProductImage existingImage = productImageRepository.findByIdAndProduct(file.id(), product);
            if (existingImage != null) {

                existingImage.setUploadOrder(counter);
                productImageRepository.save(existingImage);
                counter++;

                continue;
            }
            createImage(file, product, counter);

            counter++;
        }
    }

    private String saveImage(ProductImageRequest picture) throws IOException {
        Path uploadPath = Paths.get(uploadDirectory);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        MultipartFile file = picture.file();

        String fileName = file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName != null ? fileName : " ");
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString();
    }

    private void deleteImage(Long imageId) {
        productImageRepository.deleteById(imageId);
    }

    private void createImage(ProductImageRequest file, Product product, int counter) throws IOException {
        ProductImage productImage = new ProductImage();
        productImage.setProduct(product);
        productImage.setUploadOrder(counter);
        String filePath = saveImage(file);
        productImage.setImagePath(filePath);
        productImageRepository.save(productImage);
    }

}