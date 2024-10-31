package eshop.backend.request;

import java.util.Set;

public record ProductRequest(
        Long id,
        String name,
        String description,
        String imagePath,
        Long categoryId,
        Set<Long> relatedProductIds
) {
}