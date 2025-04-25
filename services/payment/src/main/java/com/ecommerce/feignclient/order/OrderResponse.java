package com.ecommerce.feignclient.order;

import com.ecommerce.feignclient.user.UserResponse;

import java.time.LocalDateTime;

public record OrderResponse(
        UserResponse userDetails,
        LocalDateTime createTime
) {}
