package com.ecommerce.order;

import com.ecommerce.userdetails.UserDetailsResponse;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record OrderOverviewResponse(
        Long id,
        UserDetailsResponse userDetails,
        BigDecimal totalPrice,
        LocalDateTime createTime,
        LocalDateTime updateTime,
        String status
) {}
