package eshop.backend.exception;

public class ProductNotFoundException extends Exception {
    public ProductNotFoundException(Long attributeId) {
        super(String.format("Attribute with ID %d was not found.", attributeId));
    }
}
