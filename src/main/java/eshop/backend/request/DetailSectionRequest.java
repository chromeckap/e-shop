package eshop.backend.request;


import java.util.Set;

public record DetailSectionRequest (
        Long id,
        String title,
        String description,
        Long productId
) {}
