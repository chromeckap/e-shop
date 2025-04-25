package com.ecommerce.strategy;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CourierHandler {
    CourierType value();
}
