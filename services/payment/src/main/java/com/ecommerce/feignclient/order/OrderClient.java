package com.ecommerce.feignclient.order;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service", url = "${application.config.order-url}")
public interface OrderClient {
    @GetMapping("/{id}")
    OrderResponse getOrderById(@PathVariable Long id);

}

