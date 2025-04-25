package com.ecommerce.feignclient.product;

import com.ecommerce.cartitem.CartItemRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "product-service", url = "${application.config.product-url}")
public interface ProductClient {
    @GetMapping("/{id}")
    ProductResponse getVariantById(@PathVariable Long id);

    @PostMapping("/batch")
    List<PurchaseResponse> getVariantsByCartItems(@RequestBody List<CartItemRequest> cartItems);
}
