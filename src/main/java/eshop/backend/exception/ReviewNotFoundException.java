package eshop.backend.exception;

public class ReviewNotFoundException extends Exception {
    public ReviewNotFoundException(Long attributeId) {
        super(String.format("Attribute with ID %d was not found.", attributeId));
    }
}
