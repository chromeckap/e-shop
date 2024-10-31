package eshop.backend.request;

import jakarta.annotation.Nullable;

public record OrderRequest (
        @Nullable
        Long couponId
)
{}
