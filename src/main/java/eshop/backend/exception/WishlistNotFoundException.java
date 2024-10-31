package eshop.backend.exception;

public class WishlistNotFoundException extends Exception {
    public WishlistNotFoundException(Long attributeId) {
        super(String.format("Attribute with ID %d was not found.", attributeId));
    }
}
