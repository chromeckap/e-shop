package com.ecommerce.feignclient.product;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "product-service", url = "${application.config.product-url}")
public interface ProductClient {
    @GetMapping
    Page<ProductOverviewResponse> getAllProducts(
            @RequestParam("pageNumber") int pageNumber,
            @RequestParam("pageSize") int pageSize,
            @RequestParam("direction") String direction,
            @RequestParam("attribute") String attribute
    );

    @GetMapping("/batch")
    List<ProductOverviewResponse> getProductsByIds(@RequestParam List<Long> ids);

    @GetMapping("/{id}")
    ProductOverviewResponse getProductById(@PathVariable Long id);
}
