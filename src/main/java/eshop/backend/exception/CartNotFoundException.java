package eshop.backend.exception;

public class CartNotFoundException extends Exception {
    public CartNotFoundException(Long attributeId) {
        super(String.format("Attribute with ID %d was not found.", attributeId));
    }
}
