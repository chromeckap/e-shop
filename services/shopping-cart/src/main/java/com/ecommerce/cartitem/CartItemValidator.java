package com.ecommerce.cartitem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CartItemValidator {
    public void validateQuantityIsGreaterThanZero(Integer quantity) {
        log.debug("Validating positive quantity: {}", quantity);
        if (quantity < 0) {
            throw new IllegalArgumentException("Kvantita musí být vyšší než 0.");
        }
    }
}
