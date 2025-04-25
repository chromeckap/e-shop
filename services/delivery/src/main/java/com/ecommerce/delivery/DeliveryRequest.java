package com.ecommerce.delivery;

public record DeliveryRequest(
        Long orderId,
        Long deliveryMethodId
) {}
