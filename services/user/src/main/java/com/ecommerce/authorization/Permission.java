package com.ecommerce.authorization;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Permission {
    READ("admin_read"),
    UPDATE("admin_update"),
    CREATE("admin_create"),
    DELETE("admin_delete");

    private final String permission;
}
