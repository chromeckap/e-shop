package eshop.backend.request;

import eshop.backend.enums.DiscountType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

public record DiscountRequest(
        Long id,
        String name,
        double amount,
        DiscountType type,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Set<Long> categoryIds
) {}