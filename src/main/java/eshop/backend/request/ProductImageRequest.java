package eshop.backend.request;

import org.springframework.web.multipart.MultipartFile;

public record ProductImageRequest (
        Long id,
        MultipartFile file
) {}
