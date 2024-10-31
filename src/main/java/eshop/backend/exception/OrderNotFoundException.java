package eshop.backend.exception;

public class OrderNotFoundException extends Exception {
    public OrderNotFoundException(Long attributeId) {
        super(String.format("Attribute with ID %d was not found.", attributeId));
    }
}
