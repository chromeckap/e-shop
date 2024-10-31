package eshop.backend.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    CREATED("Created"),
    PROCESSING("Processing"),
    SHIPPED("Shipped"),
    DELIVERED("Delivered"),
    CANCELED("Canceled");

    private final String status;

    OrderStatus(String status) {
        this.status = status;
    }

}
