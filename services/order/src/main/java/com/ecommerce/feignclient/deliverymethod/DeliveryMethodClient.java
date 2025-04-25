package com.ecommerce.feignclient.deliverymethod;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "delivery-method-service", url = "${application.config.delivery-method-url}")
public interface DeliveryMethodClient {
    @GetMapping("/{id}")
    DeliveryMethodResponse getDeliveryMethodById(@PathVariable Long id);

}