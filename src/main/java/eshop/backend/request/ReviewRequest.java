package eshop.backend.request;


import java.util.Set;

public record ReviewRequest (
    Long id,
    int rating,
    String text,
    Set<String> pros,
    Set<String> cons,
    Long userId,
    Long productId
) {}
