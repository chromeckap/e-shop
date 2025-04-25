package com.ecommerce.feignclient.auth;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service", url = "${application.config.auth-url}")
public interface AuthClient {

    @GetMapping("/validate")
    GatewayUserResponse validateRequest(@RequestHeader("Cookie") String cookieHeader);
}


