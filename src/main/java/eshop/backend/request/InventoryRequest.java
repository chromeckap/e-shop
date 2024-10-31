package eshop.backend.request;

import eshop.backend.enums.InventoryAction;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record InventoryRequest (
        Long variantId,
        int quantity,
        InventoryAction inventoryAction,
        LocalDateTime createdAt
) {}
