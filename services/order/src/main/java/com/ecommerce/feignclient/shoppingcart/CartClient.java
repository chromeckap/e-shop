package com.ecommerce.feignclient.shoppingcart;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cart-service", url = "${application.config.cart-url}")
public interface CartClient {
    @DeleteMapping("/{userId}/clear")
    void clearCartByUserId(@PathVariable Long userId);
}
