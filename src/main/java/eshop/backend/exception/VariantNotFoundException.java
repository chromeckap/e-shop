package eshop.backend.exception;

public class VariantNotFoundException extends Exception {
    public VariantNotFoundException(Long attributeId) {
        super(String.format("Attribute with ID %d was not found.", attributeId));
    }
}
