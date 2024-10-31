package eshop.backend.service;

import eshop.backend.enums.InventoryAction;
import eshop.backend.exception.NotEnoughVariantQuantityException;
import eshop.backend.model.Variant;
import eshop.backend.request.InventoryRequest;

/**
 * The InventoryService interface defines the methods for managing the inventory of a product variant.
 */
public interface InventoryService {
    /**
     * Creates a new inventory record for a product variant with the given request, used for a controller.
     *
     * @param request The InventoryRequest object containing the details of the inventory record.
     */
    void create(InventoryRequest request);

    /**
     * Handles the inventory action for a product variant with the given quantity, used for methods.
     *
     * @param variant The Variant object representing the product variant.
     * @param quantity The quantity of the inventory action.
     * @param inventoryAction The InventoryAction enum representing the type of inventory action.
     * @throws NotEnoughVariantQuantityException If the inventory action would result in a negative inventory quantity.
     */
    void handleInventory(Variant variant, int quantity, InventoryAction inventoryAction) throws NotEnoughVariantQuantityException;

    /**
     * Gets the total quantity of a product variant's inventory.
     *
     * @param variant The Variant object representing the product variant.
     * @return The total quantity of the product variant's inventory.
     */
    int getTotalQuantity(Variant variant);

    /**
     * Checks if a product variant is available in the inventory.
     *
     * @param variant The Variant object representing the product variant.
     * @return True if the product variant is available in the inventory, false otherwise.
     */
    boolean isVariantAvailable(Variant variant);
}
