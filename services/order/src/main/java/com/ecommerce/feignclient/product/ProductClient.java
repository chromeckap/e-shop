package com.ecommerce.feignclient.product;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Set;

@FeignClient(name = "product-service", url = "${application.config.product-url}")

public interface ProductClient {
    @GetMapping("/purchase")
    Set<PurchaseResponse> purchaseProducts(@RequestBody Set<PurchaseRequest> requests);
}
