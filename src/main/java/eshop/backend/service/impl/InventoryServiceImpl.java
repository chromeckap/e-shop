package eshop.backend.service.impl;

import eshop.backend.enums.InventoryAction;
import eshop.backend.exception.NotEnoughVariantQuantityException;
import eshop.backend.model.Inventory;
import eshop.backend.model.Variant;
import eshop.backend.repository.InventoryRepository;
import eshop.backend.repository.VariantRepository;
import eshop.backend.request.InventoryRequest;
import eshop.backend.service.InventoryService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;
    private final VariantRepository variantRepository;

    @Override
    public void create(InventoryRequest request) {
        var variant = variantRepository.findById(request.variantId())
                .orElseThrow();

        var inventory = new Inventory(request);
        inventory.setVariant(variant);

        inventoryRepository.save(inventory);
    }

    @Override
    public void handleInventory(Variant variant, @Min(1) int quantity, InventoryAction inventoryAction) throws NotEnoughVariantQuantityException {
        switch (inventoryAction) {
            case ADDED, ORDER_CANCELLED, RETURNED:
                addToInventory(variant, + quantity, inventoryAction);
                break;
            case REMOVED, ORDER_PLACED, STOLEN:
                removeFromInventory(variant, - quantity, inventoryAction);
                break;
            default:
                throw new IllegalArgumentException("Invalid inventory action for the inventory: " + inventoryAction);
        }
    }

    @Override
    public int getTotalQuantity(Variant variant) {
        return variant.getInventory().stream()
                .mapToInt(Inventory::getQuantity)
                .sum();
    }

    @Override
    public boolean isVariantAvailable(Variant variant) {
        if (variant.isUnlimitedQuantity()) //todo if unlimited, then don't change the value?
            return true;

        return getTotalQuantity(variant) > 0;
    }



    private void addToInventory(Variant variant, int quantity, InventoryAction inventoryAction) {
        if (!isPositive(quantity))
            throw new IllegalArgumentException("The quantity must be positive.");

        create(buildRequest(variant, quantity, inventoryAction));
    }

    private void removeFromInventory(Variant variant, int quantity, InventoryAction inventoryAction) throws NotEnoughVariantQuantityException {
        if (inventoryAction == InventoryAction.ORDER_PLACED && isQuantityNegativeAfterAction(variant, quantity))
            throw new NotEnoughVariantQuantityException(variant.getId());

        if (isPositive(quantity))
            throw new IllegalArgumentException("The quantity must be negative.");

        create(buildRequest(variant, quantity, inventoryAction));
    }

    private boolean isPositive(int quantity) {
        return quantity > 0;
    }

    private boolean isQuantityNegativeAfterAction(Variant variant, int quantity) {
        return getTotalQuantity(variant) < quantity;
    }

    private InventoryRequest buildRequest(Variant variant, int quantity, InventoryAction inventoryAction) {
        return InventoryRequest.builder()
                .variantId(variant.getId())
                .quantity(quantity)
                .inventoryAction(inventoryAction)
                .createdAt(LocalDateTime.now())
                .build();
    }
}