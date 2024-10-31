package eshop.backend.exception;

public class NotEnoughVariantQuantityException extends Exception {
    public NotEnoughVariantQuantityException(Long variantId) {
        super(String.format("The requested quantity of variant with id %d is not available after purchase.", variantId));
    }
}
