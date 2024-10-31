package eshop.backend.response;

import eshop.backend.model.Review;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record ReviewResponse(
        Long id,
        String text,
        LocalDateTime date,
        Set<String> pros,
        Set<String> cons,
        Long userId,
        Long productId
) {}