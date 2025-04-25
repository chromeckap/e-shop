package com.ecommerce.feignclient.product;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "${application.config.product-url}")
public interface ProductClient {
    @GetMapping("/{id}")
    ProductResponse getProductById(@PathVariable Long id);
}
