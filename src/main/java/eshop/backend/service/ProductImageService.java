package eshop.backend.service;

import eshop.backend.request.ProductImageRequest;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Set;

public interface ProductImageService {
    void manageProductPictures(Long productId, @RequestParam("files") Set<ProductImageRequest> files) throws IOException;
}
