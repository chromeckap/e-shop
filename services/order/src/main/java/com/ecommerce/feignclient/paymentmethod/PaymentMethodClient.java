package com.ecommerce.feignclient.paymentmethod;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "payment-method-service", url = "${application.config.payment-method-url}")
public interface PaymentMethodClient {
    @GetMapping("/{id}")
    PaymentMethodResponse getPaymentMethodById(@PathVariable Long id);

}
