package com.ecommerce.delivery;

import org.springframework.stereotype.Component;

@Component
public class DeliveryMapper {

    public Delivery toDelivery(DeliveryRequest request) {
        return Delivery.builder()
                .orderId(request.orderId())
                .build();
    }
}
