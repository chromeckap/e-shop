package com.ecommerce.review;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReviewResponse(
        Long id,
        int rating,
        String text,
        Long userId,
        Long productId,
        LocalDateTime createTime,
        LocalDateTime updateTime
) {}