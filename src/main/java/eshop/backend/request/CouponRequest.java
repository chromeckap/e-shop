package eshop.backend.request;

import eshop.backend.enums.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponRequest (

        String code,
        BigDecimal discount,
        DiscountType type,

        LocalDateTime expirationDate,
        int uses
) {}
