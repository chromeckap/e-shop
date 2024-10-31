package eshop.backend.exception;

public class DiscountNotFoundException extends Exception {
    public DiscountNotFoundException(Long attributeId) {
        super(String.format("Attribute with ID %d was not found.", attributeId));
    }
}