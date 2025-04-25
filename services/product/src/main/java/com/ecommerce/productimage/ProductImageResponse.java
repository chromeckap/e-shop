package com.ecommerce.productimage;

import lombok.Builder;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

@Builder
public record ProductImageResponse(
        Resource resource,
        MediaType mediaType
) {}
