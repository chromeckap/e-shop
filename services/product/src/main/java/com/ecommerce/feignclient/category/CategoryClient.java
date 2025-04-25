package com.ecommerce.feignclient.category;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

@FeignClient(name = "category-service", url = "${application.config.category-url}")
public interface CategoryClient {
    @GetMapping("/batch")
    Set<CategoryResponse> getCategoriesByIds(@RequestParam Set<Long> ids);

}
