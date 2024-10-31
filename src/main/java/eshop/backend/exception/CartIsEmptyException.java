package eshop.backend.exception;

public class CartIsEmptyException extends Exception {
    public CartIsEmptyException() {
        super("The order cannot be created if cart is empty.");
    }
}